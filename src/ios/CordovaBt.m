/********* Echo.m Cordova Plugin Implementation *******/

#import "CordovaBt.h"
#import <Cordova/CDVPlugin.h>

@implementation CordovaBt

NSString* callbackId = nil;
NSString* uuid = nil;

/*=== CORDOVA ENTRY POINT ===*/
- (void)initBt:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

/*=== CORDOVA ENTRY POINT ===*/
- (void)advertiseUuid:(CDVInvokedUrlCommand*)command
{
    NSString* myarg = [command.arguments objectAtIndex:0];
    uuid = myarg;
    self.peripheralManager = [[CBPeripheralManager alloc] initWithDelegate:self queue:dispatch_get_main_queue()];
}

/*=== CORDOVA ENTRY POINT ===*/
- (void)startDiscovery:(CDVInvokedUrlCommand*)command
{
    callbackId = command.callbackId;
    self.centralManager = [[CBCentralManager alloc] initWithDelegate:self queue:dispatch_get_main_queue()];
}

- (void)centralManagerDidUpdateState:(CBCentralManager *)central
{
    if([central state] == CBCentralManagerStatePoweredOn){
        NSLog(@"powered on bitch");
        [self.centralManager scanForPeripheralsWithServices:nil options:nil];
    }
}

- (void)peripheralManagerDidUpdateState:(CBPeripheralManager *)peripheral {
    if ([peripheral state] == CBPeripheralManagerStatePoweredOn) {
        NSLog(@"double powered on bitch");
        NSDictionary* advData = @{CBAdvertisementDataLocalNameKey: @"konferator",
                                  CBAdvertisementDataServiceUUIDsKey: @[[CBUUID UUIDWithString:uuid]]};
        [self.peripheralManager startAdvertising:advData];
    }
}

- (void)centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)peripheral advertisementData:(NSDictionary *)advertisementData RSSI:(NSNumber *)RSSI
{
    CDVPluginResult* pluginResult = nil;
    NSArray* uuids = [advertisementData valueForKey:CBAdvertisementDataServiceUUIDsKey];
    if(uuids.count > 0){
      pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[uuids[0] UUIDString]];
      [pluginResult setKeepCallbackAsBool: true];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
      NSLog(@"heh found %@", [uuids[0] UUIDString]);
    } else {
        NSLog(@"not found");
    }
}

@end
