package edu.bupt.ipoc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.bupt.ipoc.managers.ServiceManager;


public class PacketService extends Service{

	//End to end service request
	public static int CARRIED_REQUEST = 1;
	public static int REMOVED_REQUEST = 2;
	
	//service priority
	public static int SP_HIGH = 1;
	public static int SP_MID = 2;
	public static int SP_LOW = 3;
	
	//service type
	public static int ST_BEST_EFFORT = 0;
	public static int ST_LIMITATION =1;
	
	//test
	public static int STATIC_CARRIED = 1;	
	
	//data type of service, static or/and distribution data.
	public static final int D_TIME_DISTRIBUTION_ONLY = 0;//只生成时间分布的带宽数组
	public static final int D_STATIC = 1;//只生成一个静态速率
	public static final int D_DISTRIBUTION = 2;//既有时间分布的数组又有静态速率，用时间分布数组时，将statci_bw=0；


	//interactive means the service lasts forever, otherwise it will just last a while, and will be removed.
	public static final boolean INTERACTIVE = true;
	public static final boolean NON_INTERACTIVE = false;
	
	public static final int TIME_BUCKET_NUM = 240;//一个业务周期内（比如24小时）的带宽测量值的个数

	//宏定义标识，以下表示该业务将独占1G或10G等，而不论实际的速率是多少，当然实际速率一定是小于等于该值
	public static final int STATIC_1G = 1000;
	public static final int STATIC_10G = 10000;
	public static final int STATIC_40G = 40000;
	public static final int STATIC_100G = 100000;
	
	//if the init parameter for service is out of range, use the default value
	public static final int STATIC_MIN_RATE = 20;
	public static final int STATIC_MAX_RATE = STATIC_100G;
	public static final int DEFAULT_INIT_BW = 30;
	
	//The following four definitions are for making bw data.
	private static final double[] RandomArry = {1.878864133,1.841963729,1.805066644,1.768254505,1.731610656,1.695219895,1.659168205,1.623542468,1.588430169,1.553919088,1.520096989,1.487051299,1.454868778,1.423635192,1.393434979,1.36435092,1.336463804,1.309852105,1.284591659,1.260755348,1.238412799,1.217630083,1.198469436,1.180988993,1.16524253,1.151279234,1.139143483,1.128874651,1.120506934,1.114069193,1.109584828,1.107071671,1.106541905,1.108002008,1.111452722,1.116889047,1.124300266,1.133669984,1.144976205,1.15819142,1.173282729,1.190211977,1.208935911,1.229406363,1.251570431,1.275370694,1.300745416,1.327628768,1.355951043,1.385638869,1.416615406,1.448800523,1.482110953,1.51646041,1.551759654,1.587916515,1.624835848,1.662419426,1.700565754,1.739169818,1.778122764,1.817311512,1.856618335,1.895920416,1.93508942,1.973991129,2.012485197,2.050425071,2.087658176,2.124026419,2.159367098,2.19351429,2.226300787,2.257560613,2.287132163,2.314861949,2.340608908,2.364249194,2.385681301,2.404831372,2.421658437,2.436159341,2.44837308,2.458384231,2.466325196,2.472376994,2.476768367,2.479773043,2.481705089,2.482912362,2.483768183,2.484661476,2.485985696,2.488126968,2.491451952,2.49629594,2.502951767,2.511660057,2.522601279,2.535890021,2.551571767,2.569622329,2.589949966,2.612400051,2.636762045,2.662778389,2.690154851,2.718571797,2.747695813,2.777191115,2.806730228,2.836003454,2.864726763,2.892647839,2.919550123,2.94525482,2.969620968,2.992543759,3.013951403,3.033800919,3.052073264,3.068768271,3.083899869,3.09749201,3.109575734,3.120187681,3.129370277,3.137173718,3.14365971,3.148906776,3.153016814,3.156122394,3.158394209,3.160047959,3.16134991,3.162620384,3.164234496,3.166619568,3.170248891,3.175631696,3.183299513,3.193789375,3.207624618,3.225294261,3.247232132,3.273797024,3.305255147,3.34176603,3.383372882,3.429998064,3.481444053,3.537399869,3.597452579,3.66110317,3.727785794,3.79688922,3.867779224,3.939820672,4.012398145,4.084934144,4.15690414,4.227848006,4.297377656,4.365180932,4.431022041,4.494738963,4.556238394,4.615488805,4.672512189,4.727375048,4.780179037,4.831051628,4.880137012,4.927587396,4.973554736,5.018182919,5.061600356,5.103912954,5.145197438,5.185495049,5.224805658,5.263082427,5.300227163,5.336086566,5.370449607,5.403046275,5.433547925,5.461569436,5.486673334,5.508375988,5.52615586,5.539463767,5.547734954,5.550402703,5.546913138,5.536740746,5.519404132,5.494481453,5.461624947,5.420574028,5.371166396,5.31334673,5.247172564,5.172817113,5.090568862,5.000827933,4.904099321,4.800983235,4.692162893,4.578390213,4.46046991,4.339242572,4.215567291,4.090304439,3.964299133,3.838365892,3.713274909,3.589740283,3.468410437,3.349860889,3.2345894,3.123013461,3.015469989,2.912217024,2.813437181,2.719242555,2.629680784,2.544741923,2.464365845,2.388449858,2.316856297,2.249419849,2.185954439,2.126259543,2.070125813,2.017339982,1.967688999,1.920963428,1.87696013,1.875484288};
	private static final double RandMin = 1.106541905;
	private static final double RandMax = 5.550402703;
	private static final int randomArryLength = TIME_BUCKET_NUM;
	
	
	//primary parameter	
	public int id;
	public int sourceVertex;
	public int sinkVertex;
	public int s_priority = 1;
	
	//for later, if there is no enough direct resource to carry the service,
	//it can be forwarded twice or more though the transport layer
	//public int ask_time = 0;
	
	//low priority packet service may separate child service, which can be transformed though higher priority VTL.
//	public boolean is_child = false;//If true, the packet service is a child of a packet service.
//	public int father_id = -1;//If specical_child_flag is true, the father_id must have a positive num.
//	public Map<Integer,PacketService> ps_children = new HashMap<Integer,PacketService>();
	
	public VirtualTransLink carriedVTL;
	
	//default parameter
	public int service_flag = D_STATIC;
	public int static_bw = 0;
	public int[] real_time_bw = null;// = new int[time_bucket_num];
	public int bucket_count = 0;//used to count where we are in the real_time_bw, because we may start at any place
	
	public int time_long = 0;//if time_long is 0, it means forever.
	
	public int limited_bw = STATIC_100G;
	
	public double parameter = 1.0;
	
	public List<PacketServiceChild> pscs = null;
		
	public PacketService(int _id, int _sourceVertex, int _sinkVertex, int _s_priority, int _static_bw, int _time_long)
	{
		//random
		this.id = _id;
		this.sourceVertex = _sourceVertex;
		this.sinkVertex = _sinkVertex;
		this.s_priority = _s_priority;
		if(D_TIME_DISTRIBUTION_ONLY == _static_bw)
		{
			fillBwArr();
			this.service_flag = D_TIME_DISTRIBUTION_ONLY;
		}
		else if(D_STATIC == _static_bw)
		{
			this.static_bw = (int)(createRandomBasicBW()*createRandomRatioOfcrestToTrough());
			//this.service_flag = D_STATIC;
		}
		else if(_static_bw>=STATIC_MIN_RATE && _static_bw<=STATIC_MAX_RATE)
			this.static_bw = _static_bw;
		else
		{
			System.out.println("PacketService's static bw in wrong rang,the value is\t"+_static_bw);
			this.static_bw = DEFAULT_INIT_BW * (int) (Math.pow(10, s_priority))/s_priority;
		}
		
		this.time_long =  _time_long;		
	}
	
	
	private void fillBwArr() {
		this.real_time_bw = new int[TIME_BUCKET_NUM];
		createRandomPacketBW();
	}
	
	public void addStepBucketCount()
	{
		bucket_count++;
		if(bucket_count == TIME_BUCKET_NUM)
			bucket_count = 0;
	}
	
	//it will be according to the service priority
	public void createRandomPacketBW()
	{
		int x = createRandomStartPlace();
		double ratio = createRandomRatioOfcrestToTrough();
		int basicBw = createRandomBasicBW();
		double multiple_p = (ratio-1)*basicBw/(RandMax-RandMin);
		if(multiple_p<0)
			System.out.println("Something wrong:"+multiple_p);
		basicBw = basicBw- (int)(multiple_p*RandMin);
		if(real_time_bw == null)
			real_time_bw = new int[randomArryLength];
		for(int i =0;i<randomArryLength;i++)
		{
			real_time_bw[i] = (int)(basicBw+multiple_p*RandomArry[x]);
			x++;
			if(x == randomArryLength)
				x = 0;
			//System.out.print(real_time_bw[i]+"\t");
		}
		//System.out.println();
		this.static_bw = (int)(basicBw + multiple_p*RandMax);		
		//real_time_bw
	}
	
	private int createRandomStartPlace()
	{
		int _tem;
		int _tem_a = java.util.concurrent.ThreadLocalRandom.current().nextInt(2);
		if(_tem_a == 0)
			_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(randomArryLength);
		else
		{
			_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(randomArryLength/6)-24;
			if(_tem < 0)
				_tem+=randomArryLength;
		}
		return _tem;
	}
	
	private int createRandomBasicBW()
	{
		int _tem = 0;
		
		if(this.s_priority == SP_HIGH)
			_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(100,300);
		else if(this.s_priority == SP_MID)
			_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(200,400);
		else if(this.s_priority == SP_LOW)
		{
			int _tt = java.util.concurrent.ThreadLocalRandom.current().nextInt(1,100);
			if(_tt<50)
				_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(450,900);
			else
				_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(900,2250);
		}
		else
			System.out.println("Bug here, the priority should be set as one of high,mid and low");
			
		return _tem;
	}
	
	private double createRandomRatioOfcrestToTrough()
	{
		double _tem = java.util.concurrent.ThreadLocalRandom.current().nextDouble(1.1,4.5);
		return _tem;
	}
	

	

	public int getInitBW() {
		if(D_TIME_DISTRIBUTION_ONLY == service_flag)
		{
			return this.static_bw;
		}
		else
			return this.static_bw;
	}
	
	public int getStaticBw()
	{
		return this.static_bw;
	}
	
	public int getCurrentBw()
	{
		int tem_bw;
		if(this.service_flag == PacketService.D_STATIC)
			tem_bw = (int)(this.static_bw * parameter);
//need to be modified
//		else if(this.special_child_flag == true)
//			tem_bw = this.static_bw;

		else
			tem_bw = (int)(this.real_time_bw[this.bucket_count] * parameter);
		return tem_bw;
	
	}
	
	
/*	public void setCurrentBw(int _bw)
	{
		//int tem_bw;
		if(this.service_flag == PacketService.D_STATIC)
		{
			this.static_bw -= _bw;
			if(this.static_bw < 0)
				System.out.println("Wrongwrongwrong");
		}
//need to be modified
//		else if(this.special_child_flag == true)
//			tem_bw = this.static_bw;

		else
		{
			this.real_time_bw[this.bucket_count] -= _bw;
			if(this.real_time_bw[this.bucket_count]<0)

				System.out.println("Wrongwrongwrong");
		}
		//return tem_bw;
		//return false;
	
	}
	*/
	
	public void showMyselfCurrentState()
	{
		System.out.print("Id:"+this.id+"\tSource:"+this.sourceVertex+"\tSink:"+this.sinkVertex+"\t");
		
//		for(int i = 0; i < this.TIME_BUCKET_NUM;i++)
//			System.out.print(real_time_bw[i]+"\t");
		
		System.out.print("current_bw:\t"+getCurrentBw()+"\t");
		System.out.println();
	}
	
	public void showMySelfwithStaitcBW()
	{
		System.out.print("Id:"+this.id+"\tSource:"+this.sourceVertex+"\tSink:"+this.sinkVertex+"\tpriority:"+this.s_priority+"\t");
		
//		for(int i = 0; i < this.TIME_BUCKET_NUM;i++)
//			System.out.print(real_time_bw[i]+"\t");
		
		System.out.print("static_bw:\t"+this.static_bw+"\t");
		System.out.println();
	}
	
	public void cleanMyself()
	{
//		ps_children.clear();
		carriedVTL = null;
		bucket_count = 0;
		//ask_time = 0;
	}
	
	public void cleanMyselfWithoutBucketCount()
	{
//		ps_children.clear();
		carriedVTL = null;
		//bucket_count = 0;
		//ask_time = 0;
	}
	
/*	public void breakIntoSetOfChildren(boolean _static_bw_f)
	{
		ps_children.clear();
		
		int tem_bw = 0;
		int children_count = 0;
		int lingsan_bw = 0;
		int tem_id;
		PacketService tem_ps_child = null;
		if(ServiceManager.ALLOCATE_WITH_MAX_BW == _static_bw_f)
			tem_bw = this.static_bw;
		else
			tem_bw = this.real_time_bw[this.bucket_count];
		
		children_count = tem_bw/ OpticalService.BW_1G;
		if(tem_bw % OpticalService.BW_1G != 0)
		{
			children_count++;
		}
		lingsan_bw = tem_bw - ((children_count - 1) * OpticalService.BW_1G);
		
		for(int i = 0;i<children_count -1 ;i++)
		{
			tem_id = ServiceGenerator.generate_an_id();
			tem_ps_child = new PacketServiceChild(tem_id,this.sourceVertex,this.sinkVertex,OpticalService.BW_1G,this.time_long);	
			tem_ps_child.service_flag = D_STATIC;
			tem_ps_child.father_id = this.id;
			ps_children.put(tem_id, tem_ps_child);
		}
		
		tem_id = ServiceGenerator.generate_an_id();

		tem_ps_child = new PacketServiceChild(tem_id,this.sourceVertex,this.sinkVertex,lingsan_bw<STATIC_MIN_RATE?STATIC_MIN_RATE:lingsan_bw,this.time_long);
		tem_ps_child.service_flag = this.service_flag;
		tem_ps_child.special_child_flag = true;
		tem_ps_child.father_id = this.id;
		ps_children.put(tem_id, tem_ps_child);		
	}
*/	
	public boolean removeMyself()
	{
//		if(ps_children.size() == 0)
//		{
			//System.out.println("We are in remove Myselfsfsf");
			if(carriedVTL == null)
			{
				System.out.println("****\n\n\nWarning,the carriedVTL is null!\n\n");
				return false;
			}
			
			
			/*need to be modified
			for(OpticalService _os :carry_os_list.values())
			{
				//System.out.println("OS CLASS:"+_os);
				if(true == _os.removePacketService(this))
				{
					//_os.showMyself();
					cleanMyself();
					return true;
				}
			}*/
			
//		}
/*		else if(ps_children.size() >0)
		{
			//System.out.println("We are in remove Myself");
			for(PacketService _pcs : ps_children.values())
			{
				_pcs.removeMyself();
			}
			cleanMyself();
			
		}
		*/
		//System.out.println("I'm Free");

		return true;
	}

}
