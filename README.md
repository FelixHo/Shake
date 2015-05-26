 Shake 近场社交分享应用
=======
关于
--
传统蓝牙传输在不同生产商的机型中存在无法识别的情况，而NFC模块在当前手机终端设备中的应用尚未普及.基于可靠性和适用性的考虑，本应用以C/S模式实现，客户端基于Android平台开发，利用加速度传感器进行碰撞检测，基于百度定位SDK获取地理信息，结合时间参数进行匹配处理，通过ZMQ为核心的通信模式实现数据交互.  
About
--
Unrecognized circumstances exist in traditional Bluetooth models from different manufacturers, and NFC module in the current handset device is not yet universal. Based on consideration of the reliability and applicability, this application will be developed with the C / S mode, and its client will be based on the Android platform. It will use the acceleration sensor for collision detection, access geographic information based on Baidu Positioning SDK and combined with the time parameter to carry out the matching process, and to realize data interaction through ZMQ communication mode.  

更多(More)
--
####【应用框架图（Framework）】  
![framework](https://github.com/FelixHo/Shake/blob/master/raw/framework.png)  
####【核心流程（Process）】   
![process](https://github.com/FelixHo/Shake/blob/master/raw/progress.png)

Dependencies
--
[ZeroMQ](http://zguide.zeromq.org/page:all)  
[BDSDK](http://api.map.baidu.com/lbsapi/cloud/geosdk-android.htm)  

Licence
--
The MIT License (MIT)

Copyright © 2014 copyright FelixHo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
