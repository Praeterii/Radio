# Radio App

A simple Android radio application built with Jetpack Compose, Material 3, and Media3.

## Features

- **Global Radio Stations**: Lists radio stations based on locale using the [Radio Browser API](https://github.com/r-cohen/RadioBrowser-Android).
- **Background Playback**: High-quality audio streaming using ExoPlayer (Media3) that continues playing in the background via a MediaSessionService.
- **HLS Support**: Handles `.m3u8` (HLS) streams seamlessly.
- **Modern UI**: Built with Jetpack Compose and Material 3.
- **Image Loading**: Station favicons are loaded efficiently using Coil.

## Technical Stack

- **UI**: Jetpack Compose
- **Theme**: Material 3 (Custom Cyan Palette)
- **Media**: Media3 (ExoPlayer, MediaSession, HLS)
- **Image Loading**: Coil
- **Network**: Radio Browser Android SDK
- **Architecture**: MVVM with ViewModel and State

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.