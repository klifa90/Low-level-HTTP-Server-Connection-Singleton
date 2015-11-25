Low level HTTP Server Connection Singleton
=====================

Simple class for low level handling HTTP connections for posting and getting json server requests.
For UTF-8 encoding change 
			StringEntity se = new StringEntity(json.toString());
to
			StringEntity se = new StringEntity(json.toString(), "UTF-8");


Include permissions
  
    <uses-permission android:name="android.permission.INTERNET" />
 
