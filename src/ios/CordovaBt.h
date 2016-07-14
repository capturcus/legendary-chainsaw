/********* Echo.h Cordova Plugin Header *******/

#import <Cordova/CDVPlugin.h>

@import CoreBluetooth;

@interface CordovaBt : CDVPlugin <CBCentralManagerDelegate, CBPeripheralManagerDelegate>

@property (nonatomic, strong) CBCentralManager *centralManager;
@property (nonatomic, strong) CBPeripheralManager *peripheralManager;

@end
