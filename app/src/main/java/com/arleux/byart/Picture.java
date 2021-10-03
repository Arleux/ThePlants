package com.arleux.byart;

public class Picture {
    private String mPathName;
    private String mShortName;

    public Picture(String pathName){
        mPathName = "plantsPictures/"+pathName;
    }

    public String getPathName() {
        return mPathName;
    }

    public String getShortName() {
        String name[] = mPathName.split(".");
        mShortName = name[0];
        return mShortName;
    }
}
