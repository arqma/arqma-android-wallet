FROM debian:jessie

RUN apt-get update && apt-get install -y unzip automake build-essential curl cmake file pkg-config git python libtool
WORKDIR /opt/android

ENV CMAKE_VERSION 3.12.1
ENV ANDROID_NDK_REVISION 17b
ENV BOOST_VERSION 1_67_0
ENV BOOST_VERSION_DOT 1.67.0
ENV ANDROID_SDK_REVISION 4333796
ENV ANDROID_SDK_HASH 92ffee5a1d98d856634e8b71132e8a95d96c83a63fde1099be3d86df3106def9
ENV ICONV_VERSION 1.15
ENV ICONV_HASH  ccf536620a45458d26ba83887a983b96827001e92a13847b45e4925cc8913178
ENV ZLIB_VERSION 1.2.11
ENV ZLIB_HASH c3e5e9fdd5004dcb542feda5ee4f0ff0744628baf8ed2dd5d66f8ca1197cb1a1
ARG ZMQ_VERSION=v4.2.5
ARG ZMQ_HASH=d062edd8c142384792955796329baf1e5a3377cd
ARG CPPZMQ_VERSION=v4.2.3
ARG CPPZMQ_HASH=6aa3ab686e916cb0e62df7fa7d12e0b13ae9fae6
ARG SODIUM_VERSION=1.0.16
ARG SODIUM_HASH=675149b9b8b66ff44152553fb3ebf9858128363d

## INSTALL ANDROID SDK
RUN curl -s -O https://dl.google.com/android/repository/sdk-tools-linux-${ANDROID_SDK_REVISION}.zip \
    && echo "${ANDROID_SDK_HASH}  sdk-tools-linux-${ANDROID_SDK_REVISION}.zip" | sha256sum -c \
    && unzip sdk-tools-linux-${ANDROID_SDK_REVISION}.zip \
    && rm -f sdk-tools-linux-${ANDROID_SDK_REVISION}.zip

#Install cmake
RUN cd /usr \
    && curl -s -O https://cmake.org/files/v3.12/cmake-${CMAKE_VERSION}-Linux-x86_64.tar.gz \
    && tar -xzf /usr/cmake-${CMAKE_VERSION}-Linux-x86_64.tar.gz \
    && rm -f /usr/cmake-${CMAKE_VERSION}-Linux-x86_64.tar.gz
ENV PATH /usr/cmake-${CMAKE_VERSION}-Linux-x86_64/bin:$PATH

## Getting Android NDK
RUN curl -s -O https://dl.google.com/android/repository/android-ndk-r${ANDROID_NDK_REVISION}-linux-x86_64.zip \
    && unzip android-ndk-r${ANDROID_NDK_REVISION}-linux-x86_64.zip \
    && rm -f android-ndk-r${ANDROID_NDK_REVISION}-linux-x86_64.zip \
    && ln -s android-ndk-r${ANDROID_NDK_REVISION} ndk

# Build iconv for lib boost locale
RUN curl -s -O http://ftp.gnu.org/pub/gnu/libiconv/libiconv-${ICONV_VERSION}.tar.gz \
    && echo "${ICONV_HASH}  libiconv-${ICONV_VERSION}.tar.gz" | sha256sum -c \
    && tar -xzf libiconv-${ICONV_VERSION}.tar.gz \
    && rm -f libiconv-${ICONV_VERSION}.tar.gz \
    && cd libiconv-${ICONV_VERSION} \
    && CC=arm-linux-androideabi-clang CXX=arm-linux-androideabi-clang++ ./configure --build=x86_64-linux-gnu --host=arm-eabi --prefix=${PREFIX} --disable-rpath \
    && make -j${NPROC} && make install

# download, configure and make Zlib
RUN curl -s -O https://zlib.net/zlib-${ZLIB_VERSION}.tar.gz \
    && echo "${ZLIB_HASH}  zlib-${ZLIB_VERSION}.tar.gz" | sha256sum -c \
    && tar -xzf zlib-${ZLIB_VERSION}.tar.gz \
    && rm zlib-${ZLIB_VERSION}.tar.gz \
    && mv zlib-${ZLIB_VERSION} zlib \
    && cd zlib && CC=clang CXX=clang++ ./configure --static \
    && make -j${NPROC}

# ZMQ
RUN git clone https://github.com/zeromq/libzmq.git -b ${ZMQ_VERSION} \
    && cd libzmq \
    && test `git rev-parse HEAD` = ${ZMQ_HASH} || exit 1 \
    && ./autogen.sh \
    && CC=clang CXX=clang++ ./configure --prefix=${PREFIX} --host=arm-linux-androideabi --enable-static --disable-shared \
    && make -j${NPROC} \
    && make install

# zmq.hpp
RUN git clone https://github.com/zeromq/cppzmq.git -b ${CPPZMQ_VERSION} \
    && cd cppzmq \
    && test `git rev-parse HEAD` = ${CPPZMQ_HASH} || exit 1 \
    && cp *.hpp ${PREFIX}/include

# Sodium
RUN git clone https://github.com/jedisct1/libsodium.git -b ${SODIUM_VERSION} \
    && cd libsodium \
    && test `git rev-parse HEAD` = ${SODIUM_HASH} || exit 1 \
    && ./autogen.sh \
    && CC=clang CXX=clang++ ./configure --prefix=${PREFIX} --host=arm-linux-androideabi --enable-static --disable-shared \
    && make  -j${NPROC} \
    && make install

## Installing toolchains
RUN ndk/build/tools/make_standalone_toolchain.py --api 21 --stl=libc++ --arch arm --install-dir /opt/android/tool/arm \
    && ndk/build/tools/make_standalone_toolchain.py --api 21 --stl=libc++ --arch arm64 --install-dir /opt/android/tool/arm64 \
    && ndk/build/tools/make_standalone_toolchain.py --api 21 --stl=libc++ --arch x86 --install-dir /opt/android/tool/x86 \
    && ndk/build/tools/make_standalone_toolchain.py --api 21 --stl=libc++ --arch x86_64 --install-dir /opt/android/tool/x86_64

## Building OpenSSL
RUN git clone https://github.com/m2049r/android-openssl.git --depth=1 \
    && curl -L -s -O https://github.com/openssl/openssl/archive/OpenSSL_1_0_2l.tar.gz \
    && cd android-openssl \
    && tar xfz ../OpenSSL_1_0_2l.tar.gz \
    && rm -f ../OpenSSL_1_0_2l.tar.gz \
    && ANDROID_NDK_ROOT=/opt/android/ndk ./build-all-arch.sh

RUN [ "/bin/bash", "-c", "mkdir -p /opt/android/build/openssl/{arm,arm64,x86,x86_64} \
    && cp -a /opt/android/android-openssl/prebuilt/armeabi /opt/android/build/openssl/arm/lib \
    && cp -a /opt/android/android-openssl/prebuilt/arm64-v8a /opt/android/build/openssl/arm64/lib \
    && cp -a /opt/android/android-openssl/prebuilt/x86 /opt/android/build/openssl/x86/lib \
    && cp -a /opt/android/android-openssl/prebuilt/x86_64 /opt/android/build/openssl/x86_64/lib \
    && cp -aL /opt/android/android-openssl/openssl-OpenSSL_1_0_2l/include/openssl/ /opt/android/build/openssl/include \
    && ln -s /opt/android/build/openssl/include /opt/android/build/openssl/arm/include \
    && ln -s /opt/android/build/openssl/include /opt/android/build/openssl/arm64/include \
    && ln -s /opt/android/build/openssl/include /opt/android/build/openssl/x86/include \
    && ln -s /opt/android/build/openssl/include /opt/android/build/openssl/x86_64/include \
    && ln -sf /opt/android/build/openssl/include /opt/android/tool/arm/sysroot/usr/include/openssl \
    && ln -sf /opt/android/build/openssl/arm/lib/*.so /opt/android/tool/arm/sysroot/usr/lib \
    && ln -sf /opt/android/build/openssl/include /opt/android/tool/arm64/sysroot/usr/include/openssl \
    && ln -sf /opt/android/build/openssl/arm64/lib/*.so /opt/android/tool/arm64/sysroot/usr/lib \
    && ln -sf /opt/android/build/openssl/include /opt/android/tool/x86/sysroot/usr/include/openssl \
    && ln -sf /opt/android/build/openssl/x86/lib/*.so /opt/android/tool/x86/sysroot/usr/lib \
    && ln -sf /opt/android/build/openssl/include /opt/android/tool/x86_64/sysroot/usr/include/openssl \
    && ln -sf /opt/android/build/openssl/x86_64/lib/*.so /opt/android/tool/x86_64/sysroot/usr/lib64" ]

## Building Boost
RUN cd /opt/android \
    && curl -s -L -o  boost_${BOOST_VERSION}.tar.bz2 https://sourceforge.net/projects/boost/files/boost/${BOOST_VERSION_DOT}/boost_${BOOST_VERSION}.tar.bz2/download \
    && tar -xvf boost_${BOOST_VERSION}.tar.bz2 \
    && rm -f /usr/boost_${BOOST_VERSION}.tar.bz2 \
    && cd boost_${BOOST_VERSION} \
    && ./bootstrap.sh

## Building Boost
RUN cd boost_${BOOST_VERSION} \
    && PATH=/opt/android/tool/arm/arm-linux-androideabi/bin:/opt/android/tool/arm/bin:$PATH ./b2 --build-type=minimal link=static runtime-link=static --with-chrono --with-date_time --with-filesystem --with-program_options --with-regex --with-serialization --with-system --with-thread --build-dir=android-arm --prefix=/opt/android/build/boost/arm --includedir=/opt/android/build/boost/include toolset=clang threading=multi threadapi=pthread target-os=android install \
    && PATH=/opt/android/tool/arm64/aarch64-linux-android/bin:/opt/android/tool/arm64/bin:$PATH ./b2 --build-type=minimal link=static runtime-link=static --with-chrono --with-date_time --with-filesystem --with-program_options --with-regex --with-serialization --with-system --with-thread --build-dir=android-arm64 --prefix=/opt/android/build/boost/arm64 --includedir=/opt/android/build/boost/include toolset=clang threading=multi threadapi=pthread target-os=android install \
    && PATH=/opt/android/tool/x86/i686-linux-android/bin:/opt/android/tool/x86/bin:$PATH ./b2 --build-type=minimal link=static runtime-link=static --with-chrono --with-date_time --with-filesystem --with-program_options --with-regex --with-serialization --with-system --with-thread --build-dir=android-x86 --prefix=/opt/android/build/boost/x86 --includedir=/opt/android/build/boost/include toolset=clang threading=multi threadapi=pthread target-os=android install \
    && PATH=/opt/android/tool/x86_64/x86_64-linux-android/bin:/opt/android/tool/x86_64/bin:$PATH ./b2 --build-type=minimal link=static runtime-link=static --with-chrono --with-date_time --with-filesystem --with-program_options --with-regex --with-serialization --with-system --with-thread --build-dir=android-x86_64 --prefix=/opt/android/build/boost/x86_64 --includedir=/opt/android/build/boost/include toolset=clang threading=multi threadapi=pthread target-os=android install \
    && ln -sf ../include /opt/android/build/boost/arm \
    && ln -sf ../include /opt/android/build/boost/arm64 \
    && ln -sf ../include /opt/android/build/boost/x86 \
    && ln -sf ../include /opt/android/build/boost/x86_64

# Building Arqma from the latest commit on Android branch, this should be kept up to date with master
# with the necessary fixes by malbit and m2049r to build for mobile.
RUN cd /opt/android \
    && git clone -b release-v0.1.3.0 https://github.com/arqma/arqma.git --recursive --depth=5 \
    && cd arqma \
    && mkdir -p build/release \
    && bash build-all-arch.sh
