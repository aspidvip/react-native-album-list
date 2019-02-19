//
//  RNAlbumOptions.h
//  RNAlbumsModule
//
//  Created by edison on 22/02/2017.
//  Copyright © 2017 edison. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, RNAlbumOptions) {
  OptionCount,
  OptionThumbnail,
  OptionThumbnailDimensions,
};

static NSString *stringFromOption(RNAlbumOptions option);
static BOOL optionExists(RNAlbumOptions option, NSDictionary *options);
