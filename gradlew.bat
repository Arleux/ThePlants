<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.lope">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:label="Life Cycle Test"
            android:name=".LifeCycleTest"
            android:configChanges="keyboard|keyboardHidden|orientation" />
        <activity android:label="Single Touch Test"
            android:name=".SingleTouchTest"/>
        <activity android:label="Multy Touch Test"
            android:name=".MultyTouchTest"/>
        <activity android:label="Key Test"
            android:name=".KeyTest"/>
        <activity android:label="Accelerometer Test"
            android:name=".AccelerometerTest"/>
        <activity android:label="Assets Test"
            android:name=".AssetsTest"/>
    </application>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> //доступ к SD-карте
</manifest>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          