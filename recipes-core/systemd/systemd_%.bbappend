# Enable systemd's own network + DNS stack on the main systemd package.
#
# OE ships a "disable *" default preset, so networkd/resolved stay off
# unless explicitly enabled. We do it here rather than via a preset in
# cogip-net because SYSTEMD_SERVICE makes the systemd class verify at
# do_package time that the units actually exist in the package: if the
# networkd/resolved PACKAGECONFIG ever drops out, the build fails loudly
# instead of silently producing an image with no network at first boot.
#
# Guarded on PACKAGECONFIG so the units are only referenced when they
# are actually built.
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'networkd', 'systemd-networkd.service', '', d)}"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'resolved', 'systemd-resolved.service', '', d)}"

# Persistent journal: keep logs on /var/log/journal so they survive a
# power cycle and can be read off the SD card on a host -- essential to
# debug a headless board with no serial/network access.
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://persistent-journal.conf"

# Console policy: this is a kiosk: the HDMI VT (tty1) is owned by Cog,
# which does a VT hangup to take DRM master. systemd otherwise enables a
# login getty on tty1 (vendor symlink getty.target.wants/[email protected]),
# and the two crash-loop on /dev/tty1 until both hit the start limit ->
# black screen. OE has no dedicated knob for a VT getty under systemd
# (SERIAL_CONSOLES is serial-only, USE_VT is sysvinit-only), so mask the
# unit -- systemd's documented "never start this" mechanism, which also
# overrides any logind autovt spawn. Login stays available over serial
# and SSH.

do_install:append() {
    install -d ${D}${sysconfdir}/systemd/journald.conf.d
    install -m 0644 ${UNPACKDIR}/persistent-journal.conf \
        ${D}${sysconfdir}/systemd/journald.conf.d/10-persistent.conf

    install -d ${D}${sysconfdir}/systemd/system
    ln -sf /dev/null ${D}${sysconfdir}/systemd/system/getty@tty1.service
}

FILES:${PN} += " \
    ${sysconfdir}/systemd/journald.conf.d/10-persistent.conf \
    ${sysconfdir}/systemd/system/getty@tty1.service \
"
