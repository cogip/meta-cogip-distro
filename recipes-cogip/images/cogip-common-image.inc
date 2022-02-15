DESCRIPTION = "COGIP image base"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit core-image

# Display
IMAGE_FEATURES += " \
    weston \
"
IMAGE_INSTALL += " \
    matchbox-terminal \
"

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

# Development
IMAGE_INSTALL += " \
    python3-pip \
"

# Editors
IMAGE_INSTALL += " \
    vim \
"
