// My QRcode: 1491800B82 0.95 **** 25.8
// 14918 is produce at 2014-09-18 (WTF!)
// 0.95 is reference voltage
// 25.8 is temperature of reference voltage
//包含头文件
#include <LiquidCrystal.h>
//定义使用的I/O口
LiquidCrystal lcd(12,11,5,4,3,2);

#define measurePin A5 // 连接模拟口0
 
#define ledPower  8 // 连接数字口2

int count = 0;
float averageP = 0; // reading of DN7C3
float valueP = 0;
float historyP = 0;
float Ps[100];
unsigned long timer = 0;
 
unsigned long A = 0;
unsigned long B = 0;
 
void setup() {
  
    //设置lcd显示的行数与列数
    lcd.begin(16,2);
    lcd.print("PM2.5 Sensor");
    pinMode(measurePin, INPUT); // DN7C3 Vo Pin
    pinMode(ledPower, OUTPUT);
    digitalWrite(ledPower, HIGH);
 
    Serial.begin(115200);
    Serial.println("Sharp DN7C3 PM2.5 Sensor");
    
    for(int i = 0; i < 1000; i++) {
        delayMicroseconds(9680);
        digitalWrite(ledPower, LOW);
        delayMicroseconds(320);
        digitalWrite(ledPower, HIGH);
    }
    for(int i = 0; i < 100; i++){
      Ps[i] = 0.0;
    }
    timer = micros();
}
 
void loop() {
    if ((micros() - timer) >= 10000) {  // per 10ms
        timer = micros();
 
        digitalWrite(ledPower, LOW);    // turn on led
        A = micros();            // measure pulse begin time
        delayMicroseconds(227);  // Arduino ADC need about 100us
        valueP = analogRead(measurePin); // PM2.5 reading
        B = micros();            // measure pulse end time
        digitalWrite(ledPower, HIGH);   // turn off led
        Ps[count] =  valueP * (5000.0 / 1024.0);
        count++;
        //averageP = (averageP * (count -1) +  valueP * (5000.0 / 1024.0))/count;
        if (count >= 100) { // 100 times average, 1 second
            float largest = 0.0;
            float lowest = 5000.0;
            int average_num = 0;
            for(int i = 0; i < 100; i++){
              float j = Ps[i];
              if(j > largest){
                largest = j;
              }
              if(j < lowest){
                lowest = j;
              }
            }
            for(int i = 0; i < 100; i++){
              float j = Ps[i];
              if(lowest< j < largest){
                averageP += j;
                average_num++;
              }
            }
            averageP = averageP / average_num;
            float temp = 16.0; // get Temperature
            float humidity = 76.0;
            float diffrent = averageP - (930 + (temp-25.8)*6); // 1100 is ture reference voltage base on my circuits
            float h_factor = 1.0;
            if(humidity <= 50){
              h_factor = 1;
            }else{
              h_factor = 1.0-0.01467*(humidity-50);
            }
            float pm25ug = h_factor * diffrent * 0.6;
 
            if (historyP == 0) {
                historyP = pm25ug;
            }
            else {
                historyP = (historyP * 0.95 + pm25ug * 0.05);
            }
            lcd.clear();
            lcd.setCursor(0,0);
            lcd.print("PM25: ");
            lcd.print(historyP); // averaged
            lcd.setCursor(14,0);
            lcd.print("ug");
            lcd.setCursor(0,1);
            lcd.print("Volt: ");
            lcd.print(averageP);
            lcd.setCursor(14,1);
            lcd.print("mV");
            // post to Serial
            Serial.print("T: ");
            Serial.print(temp, 2);
            Serial.print(" C\tV: ");
            Serial.print(averageP, 2);
            Serial.print(" mV\t P: ");
            Serial.print(pm25ug, 2);
            Serial.print(" ug/m3\t avgP: ");
            Serial.print(historyP, 2);
            Serial.print(" ug/m3\t Pulse: ");
            Serial.println(B - A);
 
            // clear for next time
            count = 0;
            valueP = 0;
            averageP = 0;
        }
    }
}
