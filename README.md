# React Native Album List

Этот компонент был скопирован из react-native-albums и немного доработан, чтобы мождно было его использовать.

# Installation

Install the package from npm:

`yarn add --save react-native-album-list` or `npm i --save react-native-album-list`

and

`react-native link`

# Exemple

`import AlbumsList from 'react-native-album-list'`

`AlbumsList.getAlbumList({`
`count: true,`
`thumbnail: false,`
`thumbnailDimensions: false,`
`}).then(list => console.log(list));`
