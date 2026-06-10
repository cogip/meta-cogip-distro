# Enable the per-interface wpa_supplicant template for wlan0 so Wi-Fi
# associates at boot, feeding systemd-networkd's 30-wireless.network.
#
# Only enabled when WLAN_SSID is set at build time (Ethernet-only
# images leave it off). Done here rather than via a preset so the
# systemd class verifies the wpa_supplicant@.service template exists
# at do_package time.
SYSTEMD_SERVICE:${PN} += "${@'wpa_supplicant@wlan0.service' if d.getVar('WLAN_SSID') else ''}"

WLAN_SSID ??= ""

# Clear rfkill soft-blocks before wpa_supplicant@wlan0 starts (the Pi 4
# Wi-Fi can boot soft-blocked, which prevents association).
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "${@' file://unblock-rfkill.conf' if d.getVar('WLAN_SSID') else ''}"
RDEPENDS:${PN} += "${@' rfkill' if d.getVar('WLAN_SSID') else ''}"

do_install:append() {
    if [ -n "${WLAN_SSID}" ]; then
        install -d ${D}${systemd_system_unitdir}/wpa_supplicant@wlan0.service.d
        install -m 0644 ${WORKDIR}/unblock-rfkill.conf \
            ${D}${systemd_system_unitdir}/wpa_supplicant@wlan0.service.d/unblock-rfkill.conf
    fi
}

FILES:${PN} += "${systemd_system_unitdir}/wpa_supplicant@wlan0.service.d"
