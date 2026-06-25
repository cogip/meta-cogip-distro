SUMMARY = "uv - an extremely fast Python package and project manager"
DESCRIPTION = "Prebuilt aarch64 uv binary from Astral's GitHub release. On the \
board it backs the /opt/.venv experimentation venv: `uv pip install <pkg>` \
lets Eric try a dependency without rebuilding the image. uv is NOT used to \
build the image or resolve the runtime deps (those are the Yocto wheels) -- \
only as the on-device package manager for the venv."
HOMEPAGE = "https://github.com/astral-sh/uv"
LICENSE = "Apache-2.0 | MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# Prebuilt release tarball (extracts to uv-aarch64-unknown-linux-gnu/{uv,uvx}).
SRC_URI = "https://github.com/astral-sh/uv/releases/download/${PV}/uv-aarch64-unknown-linux-gnu.tar.gz"
SRC_URI[sha256sum] = "d89430e201f629b203975c605cd6bfe85afc2bc0781d95838e2b5177a03b1545"

S = "${UNPACKDIR}/uv-aarch64-unknown-linux-gnu"

# It is a prebuilt aarch64 ELF: only valid on the target arch, and Yocto did
# not compile it, so bypass the strip/QA steps that assume otherwise.
COMPATIBLE_HOST = "aarch64.*-linux"
PACKAGE_ARCH = "${TUNE_PKGARCH}"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "already-stripped ldflags"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/uv  ${D}${bindir}/uv
    install -m 0755 ${S}/uvx ${D}${bindir}/uvx
}

FILES:${PN} = "${bindir}/uv ${bindir}/uvx"
