SUMMARY = "Cogip default systemd-networkd configuration"
DESCRIPTION = "Drops .network files for wired + wireless (DHCP), links \
resolv.conf to systemd-resolved's stub, and generates the wpa_supplicant \
config for wlan0 (base config always; network block when WLAN_SSID is \
set). networkd/resolved are enabled by the systemd bbappend and \
wpa_supplicant@wlan0 by the wpa-supplicant bbappend."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://20-wired.network \
    file://30-wireless.network \
"

S = "${WORKDIR}"

RDEPENDS:${PN} = "systemd"

inherit allarch

# Wi-Fi credentials come from the build environment (see kas-cogip.yml
# env block). WLAN_PSK is the hashed PSK (wpa_passphrase output), not
# the plaintext passphrase. Empty SSID => Ethernet-only image.
WLAN_SSID ??= ""
WLAN_PSK  ??= ""

do_install() {
    install -d ${D}${sysconfdir}/systemd/network
    install -m 0644 ${WORKDIR}/20-wired.network \
                    ${D}${sysconfdir}/systemd/network/20-wired.network
    install -m 0644 ${WORKDIR}/30-wireless.network \
                    ${D}${sysconfdir}/systemd/network/30-wireless.network

    # Point /etc/resolv.conf at the stub resolver maintained by
    # systemd-resolved so glibc resolves through it.
    install -d ${D}${sysconfdir}
    ln -sf /run/systemd/resolve/stub-resolv.conf \
           ${D}${sysconfdir}/resolv.conf

    # Generate the per-interface wpa_supplicant config consumed by
    # wpa_supplicant@wlan0.service. Write the base config unconditionally
    # (cheap, harmless on Ethernet-only Pis) and add the network block
    # only when an SSID was provided; that way the supplicant always has
    # a valid -c file even if the unit is enabled by hand.
    # printf (not heredoc): bitbake's task parser chokes on heredocs.
    install -d ${D}${sysconfdir}/wpa_supplicant
    conf="${D}${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf"
    printf 'ctrl_interface=/run/wpa_supplicant\n' > "$conf"
    printf 'update_config=1\n' >> "$conf"
    printf 'country=FR\n' >> "$conf"
    if [ -n "${WLAN_SSID}" ]; then
        printf '\nnetwork={\n' >> "$conf"
        printf '    ssid="%s"\n' "${WLAN_SSID}" >> "$conf"
        printf '    psk=%s\n' "${WLAN_PSK}" >> "$conf"
        printf '}\n' >> "$conf"
    fi
    chmod 0600 "$conf"
}

FILES:${PN} = " \
    ${sysconfdir}/systemd/network/20-wired.network \
    ${sysconfdir}/systemd/network/30-wireless.network \
    ${sysconfdir}/resolv.conf \
    ${sysconfdir}/wpa_supplicant \
"

# The PSK lands in the package output, so its hash depends on the
# credentials: keep this recipe out of any shared/published sstate
# mirror to avoid leaking secrets.
do_install[vardeps] += "WLAN_SSID WLAN_PSK"
