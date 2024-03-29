DESCRIPTION = "COGIP image base"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

require recipes-core/images/core-image-base.bb

# Kernel
IMAGE_INSTALL += " \
    kernel-modules \
"

# Network
IMAGE_FEATURES += " \
    ssh-server-openssh \
"

# Wireless
IMAGE_INSTALL += " \
    crda \
"
