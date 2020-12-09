# Building external libs in Docker

Builds Arqma libs for all Android architectures from `https://github.com/malbit/arqma.git`, `android` branch.

```Shell
docker build -t arqma-android-image -f ../android-docker .
```

Create container to copy libs:
```Shell
docker create --name arqma-android arqma-android-image
```
 
Launch collecting script from `android-libs` directory:
```Shell
./collect.sh arqma-android 
```
