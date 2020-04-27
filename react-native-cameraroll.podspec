require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "react-native-cameraroll"
  s.version      = package['version']
  s.summary      = package['description']
  s.license      = package['license']

  s.authors      = package['author']
  s.homepage     = package['homepage']
  s.platform     = :ios, "9.0"

  s.source       = { :git => "https://github.com/aspidvip/react-native-album-list", :tag => "#{s.version}" }
  s.source_files  = "ios/RNAlbumsModule/*.{h,m}"

  s.dependency 'React'
end
