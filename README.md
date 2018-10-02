# Arqma Android Wallet
Android Wallet for Arqma

### QUICKSTART
- Download the APK for the most current release [here](https://github.com/arqma/arqma-android-wallet/releases) and install it
- Run the App and select "Generate Wallet" to create a new wallet or recover a wallet
- Advanced users can copy over synced wallet files (all files) onto sdcard in directory arqma-wallet (created first time App is started)
- See the [FAQ](doc/FAQ.md)

## Translations
Help us translate Arqma Wallet!

### Disclaimer
You may lose all your Arqma if you use this App. Be cautious when spending on the mainnet.

### Random Notes
- currently only android32 (runs on 64-bit as well)
- works on the stagenet & mainnet
- sync is slow due to 32-bit architecture
- use your own daemon - it's easy
- screen stays on until first sync is complete

### TODO
- see issues on github

### Issues / Pitfalls
- Users of Zenfone MAX & Zenfone 2 Laser (possibly others) **MUST** use the armeabi-v7a APK as the arm64-v8a build uses hardware AES
functionality these models don't have.
- You should backup your wallet files in the "arqma-wallet" folder periodically.
- Also note, that on some devices the backups will only be visible on a PC over USB after a reboot of the device (it's an Android bug/feature)

### HOW TO BUILD
If you want to build yourself (recommended) check out [the instructions](doc/BUILDING-external-libs.md)

Then, fire up Android Studio and build the APK.
