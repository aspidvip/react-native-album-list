# React Native Album List

A library for getting all the titles of photo albums and photos.
This library was taken https://github.com/shimohq/react-native-albums and modified for further work.

# Installation

Install the package from npm:

`yarn add --save react-native-album-list` or `npm i --save react-native-album-list`

and

`react-native link`

# Example

`import AlbumsList from 'react-native-album-list'`

Get a list of albums

```js
AlbumsList.getAlbumList({
  count: true,
  thumbnail: false,
  thumbnailDimensions: false
}).then(list => console.log(list));
```

### getAlbumList options

| Attribute             | Values             |
| --------------------- | ------------------ |
| `count`               | `'true'`/`'false'` |
| `thumbnail`           | `'true'`/`'false'` |
| `thumbnailDimensions` | `'true'`/`'false'` |

Get a list of photos

```js
AlbumsList.getImageList({
  title: true,
  name: false,
  size: true,
  description: true,
  location: false,
  date: true,
  orientation: true,
  type: false,
  album: true,
  dimensions: false
}).then(list => console.log(list));
```

### imageListOptions options

| Attribute     | Values             |
| ------------- | ------------------ |
| `title`       | `'true'`/`'false'` |
| `name`        | `'true'`/`'false'` |
| `size`        | `'true'`/`'false'` |
| `description` | `'true'`/`'false'` |
| `location`    | `'true'`/`'false'` |
| `date`        | `'true'`/`'false'` |
| `orientation` | `'true'`/`'false'` |
| `type`        | `'true'`/`'false'` |
| `album`       | `'true'`/`'false'` |
| `dimensions`  | `'true'`/`'false'` |
