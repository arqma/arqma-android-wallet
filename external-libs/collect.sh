#!/bin/bash
packages=(boost openssl arqma libsodium libzmq)
archs=(arm arm64 x86 x86_64)
#archs=(x86)

docker container rm arqma-android > /dev/null 2>&1

set -e

docker create --name arqma-android arqma-android-image

build_dir=`pwd`/tmp_build
rm -Rf $build_dir
docker cp arqma-android:/opt/android/build $build_dir

for arch in ${archs[@]}; do
    case ${arch} in
        "arm")
			xarch="armeabi-v7a"
			;;
        "arm64")
			xarch="arm64-v8a"
            ;;
        "x86")
			xarch="x86"
            ;;
        "x86_64")
			xarch="x86_64"
            ;;
        *)
			exit 16
            ;;
    esac

    for package in ${packages[@]}; do
    INPUT_DIR=`pwd`/build/build/$package
    OUTPUT_DIR=`pwd`/$package/lib/$xarch
    mkdir -p $OUTPUT_DIR
    rm -f $OUTPUT_DIR/*.a
    cp -a $build_dir/$package/$arch/lib/*.a $OUTPUT_DIR

    if [ $package = "arqma" ]; then
      rm -rf $OUTPUT_DIR/../../include
      cp -a $build_dir/$package/include $OUTPUT_DIR/../..
    fi

    done
done

rm -rf $build_dir
docker container rm arqma-android

exit 0

