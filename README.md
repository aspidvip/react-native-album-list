# React Native Album List

Этот компонент был скопирован из react-native-albums и немного доработан, чтобы можно было его использовать.

# Installation

Install the package from npm:

`yarn add --save react-native-album-list` or `npm i --save react-native-album-list`

and

`react-native link`

# Exemple

`import AlbumsList from 'react-native-album-list'`

Получить список альбомов

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

Получить список фотографий

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
