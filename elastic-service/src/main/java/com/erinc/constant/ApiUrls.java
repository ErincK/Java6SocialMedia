package com.erinc.constant;

public class ApiUrls {

    public static final String VERSION="api/v1";
    public static final String ELASTIC=VERSION+"/elastic";
    public static final String USER=ELASTIC+"/elastic";


    //UserController
    public static final String CREATE="/create";
    public static final String UPDATE="/update";
    public static final String DELETEBYID="/deletebyid";
    public static final String FINDBYID="/findbyid";
    public static final String FINDALL="/findall";
    public static final String ACTIVATESTATUS="/activatestatus";

    public static final String ACTIVATESTATUS2="/activatestatus/{authId}";
    public static final String FINDBYROLE="/findbyrole";
}
