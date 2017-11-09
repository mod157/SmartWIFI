# FiCatch(파이캐치)
-  주변 WIFI를 잡아 자동으로 연결해줍니다.
-  등록하기 전 휴대폰의 관한 설정을 함으로써 주변의 등록한 WIFI가 존재한다면 휴대폰 설정을 변경해줍니다.
-
설명
사용자가 WIFI zone 안에서 수동으로 WIFI를 연결하는 방식을 벗어나서 자동으로 자신이 지정한 WIFI를 잡을 수 있게 한다. 또 Wifi Zone 안에서 사용자가 스마트폰의 설정을 입력한다면, 스마트폰이 해당 Wifi를 다시 인식할 때 사용자가 설정한 스마트폰의 설정이 활성화 되는 모바일 어플리케이션을 구현
 
기능
Service에서 주기적으로 주변의 Wifi를 Scan하여 현재 사용자가 설정한 List와 비교하여 원하는 설정으로 변경
WIFI라는 위치정보를 이용하여 원하는 위치에서 휴대폰 설정을 변경


기간: 2017.02~2017.02

개발환경:
Android OS 4.2 (Jelly Bean) 이상의 안드로이드 기기
Android Studio
WIFI zone

주요기술: WifiScan, System Settings, SQLiteDB, Service

앱 소개 링크 : https://mod157.github.io/SmartWIFI/
