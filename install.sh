APPNAME=knitknit
ACTIVITY=ProjectList
adb -d install -r bin/$APPNAME-debug.apk
adb -d shell am start -a android.intent.action.MAIN -n com.example.$APPNAME/.$ACTIVITY
