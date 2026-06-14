SUMMARY = "uv - an extremely fast Python package and project manager (Astral)"
DESCRIPTION = "Prebuilt uv release binary, used to build/install the COGIP \
tools virtualenv on the target (see the cogip-tools recipe). Pinned to \
the same uv version as cogip-tools' Dockerfile so the venv builds the \
same way."
HOMEPAGE = "https://github.com/astral-sh/uv"
LICENSE = "Apache-2.0 | MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Prebuilt static Rust binary from the GitHub release. aarch64 only for
# now (the kiosk machine is raspberrypi4-64); generalise the triple if we
# ever target another arch.
SRC_URI = "https://github.com/astral-sh/uv/releases/download/${PV}/uv-aarch64-unknown-linux-gnu.tar.gz"
# Canonical upstream sha256 (matches Astral's published .sha256 sidecar).
SRC_URI[sha256sum] = "d89430e201f629b203975c605cd6bfe85afc2bc0781d95838e2b5177a03b1545"

COMPATIBLE_HOST = "aarch64.*-linux"

# The tarball extracts to uv-aarch64-unknown-linux-gnu/{uv,uvx}.
S = "${UNPACKDIR}/uv-aarch64-unknown-linux-gnu"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/uv ${D}${bindir}/uv
    install -m 0755 ${S}/uvx ${D}${bindir}/uvx
}

FILES:${PN} = "${bindir}/uv ${bindir}/uvx"

# Prebuilt upstream binary: don't run the usual ELF QA on it.
INSANE_SKIP:${PN} += "already-stripped ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
