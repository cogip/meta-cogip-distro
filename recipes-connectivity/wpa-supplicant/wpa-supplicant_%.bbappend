# Enable the per-interface wpa_supplicant template for wlan0 so Wi-Fi
# associates at boot, feeding systemd-networkd's 30-wireless.network.
#
# Gated on WLAN_SSID so Ethernet-only images leave Wi-Fi off. When Wi-Fi
# is configured we REPLACE the stock SYSTEMD_SERVICE (so only wlan0's
# template is managed, not the generic wpa_supplicant.service which would
# fight networkd over wlan0) AND flip SYSTEMD_AUTO_ENABLE to "enable":
# the stock recipe ships it "disable", so merely adding the unit to
# SYSTEMD_SERVICE leaves it packaged-but-disabled -- which is exactly why
# the kiosk booted with the radio up, the rfkill drop-in installed, yet
# the supplicant never enabled (systemctl showed disabled/preset:disabled).
WLAN_SSID ??= ""
SYSTEMD_SERVICE:${PN} = "${@'wpa_supplicant@wlan0.service' if d.getVar('WLAN_SSID') else 'wpa_supplicant.service'}"
SYSTEMD_AUTO_ENABLE = "${@'enable' if d.getVar('WLAN_SSID') else 'disable'}"

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
