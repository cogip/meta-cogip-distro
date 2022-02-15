DESCRIPTION = "COGIP Cortex image"

require cogip-common-image.inc

IMAGE_INSTALL += " \
    systemd-analyze \
    openocd \
"

#IMAGE_INSTALL += " \
#    python3-click \
#    python3-jinja2 \
#    python3-psutil \
#    python3-pyserial \
#    python3-python-dotenv \
#    python3-typer \
#"
