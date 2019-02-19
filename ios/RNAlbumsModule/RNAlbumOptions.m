//
//  RNAlbumOptions.m
//  RNAlbumsModule
//
//  Created by edison on 22/02/2017.
//  Copyright © 2017 edison. All rights reserved.
//

#import "RNAlbumOptions.h"
#import <Photos/Photos.h>

static NSString *stringFromOption(RNAlbumOptions option) {
  switch (option) {
    case OptionCount: return @"count";
    case OptionThumbnail: return @"thumbnail";
    case OptionThumbnailDimensions: return @"thumbnailDimensions";
  }
}

static BOOL optionExists(RNAlbumOptions option, NSDictionary *options) {
  return [options[stringFromOption(option)] boolValue];
}
