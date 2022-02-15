FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://wpa_supplicant.conf \
    file://wpa_supplicant-failsafe-ap.conf \
"

do_install:append() {
    install -d ${D}${sysconfdir}/wpa_supplicant/

    # Default configuration
	install -m 600 ${WORKDIR}/wpa_supplicant.conf \
        ${D}${sysconfdir}/wpa_supplicant/wpa_supplicant.conf

    # Failsafe configuration
	install -m 600 ${WORKDIR}/wpa_supplicant-failsafe-ap.conf \
        ${D}${sysconfdir}/wpa_supplicant/wpa_supplicant-failsafe-ap.conf
}
