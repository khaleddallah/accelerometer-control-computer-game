## Mobile accelerometer to control computer race games :
1. mobile read accelerometer value (x,y,z angle value) and send it to computer throw Udp protocol
2. computer take the value of accelerometer and decide the direction then simulate the convenient key (a,s,d,w) from keyboard.  

## technical review :  
- in computer section (labview program) : there is Udp server and keyboard simulator .  
- in mobile section (android app) : there is accelerometer reader and Udp client .     

## Usage :  
0. your mobile and computer should be in the same network .  
1. open **UdpServer_KeySim.vi** with labview.  
2. in **Block Diagram window" put your local ip address (like 192.168.1.4) and your port (like 9999).  
3. install **Acc_UdpClient.apk** and open it , then put the same ip and port in boxes.,then click send.
*  now (a , s , d , w) is clicking  proportionally with mobile angle . 