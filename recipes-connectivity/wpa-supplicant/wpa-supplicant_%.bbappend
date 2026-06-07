# Enable the per-interface wpa_supplicant template for wlan0 so Wi-Fi
# associates at boot, feeding systemd-networkd's 30-wireless.network.
#
# Only enabled when WLAN_SSID is set at build time (Ethernet-only
# images leave it off). Done here rather than via a preset so the
# systemd class verifies the wpa_supplicant@.service template exists
# at do_package time.
SYSTEMD_SERVICE:${PN} += "${@'wpa_supplicant@wlan0.service' if d.getVar('WLAN_SSID') else ''}"

WLAN_SSID ??= ""
