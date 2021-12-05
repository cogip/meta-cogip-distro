# Avoid using busybox 'less' command to allow colors to be displayed
RDEPENDS_${PN} += " \
    less \
"
