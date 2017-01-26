package edu.bupt.ipoc.service;

public class Service {
	
	public static int PRIORITY_HIGH  = 1;
	public static int PRIORITY_MID  = 2;
	public static int PRIORITY_LOW  = 3;
	public static int RANDOM_PRIORITY = -1;
	
	//bucket_num for one day
	public static final int TIME_BUCKET_NUM = 240;
	public static final int TIME_STEP = 1;//60 secends
	
	public static final int BW_1G = 1000;
	public static final int BW_10G = 10000;
	public static final int BW_40G = 40000;
	public static final int BW_100G = 100000;
	
	public static final int MAX_ALLOWED_BW = BW_10G;
	
	public static final int STATIC_MIN_RATE = 50;
	public static final int MIN_SHARED_BW_GRANULARITY = STATIC_MIN_RATE;
	

	public static final double REGULAR_UPPER_THRESHOLD = 0.85;	
	public static final double SURVIVABILITY_FACTOR = 1.0/REGULAR_UPPER_THRESHOLD;
	
	public static final int TYPE_OTN = 1;
	
	public static final int OTN_BUILD_REQUEST = 1;
	public static final int PS_CARRIED_REQUEST = 2;
	public static final int PS_REMOVED_REQUEST = 3;
	public static final int UseOpticalService = 11;
	public static final int UseOTNService = 22;
	
	public static final int NEED_TO_BE_REMOVED = 1;
	public static final int STILL_RUNNING = 0;
	

	public static final double TH_USP_LOW = 0.95;

}
