package edu.bupt.ipoc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.managers.ServiceManager;
import edu.bupt.ipoc.service.*;

public class ServiceGenerator {
	
	//End to end service generator, the service can be packet service or packet service
	public static final int SLEEP_BASE_NUM = 100;
	public static final int LASTTIME_BASE_NUM = 12000;
	
	public static final int SERVICE_WITH_RANDOM_BW = 0;
	public static final int SERVICE_WITH_STATIC_BW = 1;
	
	public static final int PERMENENT_SERVICE = 0;
	public static final int NOT_PERMENENT_SERVICE = 1;
	public static final int MIED_SERVICE = 2;
	

	
	
	//The following four definitions are for making bw data. The array length should be bigger than TIME_BUCKET_NUM;
	private static final double[] RandomArry = {1.878864133,1.841963729,1.805066644,1.768254505,1.731610656,1.695219895,1.659168205,1.623542468,1.588430169,1.553919088,1.520096989,1.487051299,1.454868778,1.423635192,1.393434979,1.36435092,1.336463804,1.309852105,1.284591659,1.260755348,1.238412799,1.217630083,1.198469436,1.180988993,1.16524253,1.151279234,1.139143483,1.128874651,1.120506934,1.114069193,1.109584828,1.107071671,1.106541905,1.108002008,1.111452722,1.116889047,1.124300266,1.133669984,1.144976205,1.15819142,1.173282729,1.190211977,1.208935911,1.229406363,1.251570431,1.275370694,1.300745416,1.327628768,1.355951043,1.385638869,1.416615406,1.448800523,1.482110953,1.51646041,1.551759654,1.587916515,1.624835848,1.662419426,1.700565754,1.739169818,1.778122764,1.817311512,1.856618335,1.895920416,1.93508942,1.973991129,2.012485197,2.050425071,2.087658176,2.124026419,2.159367098,2.19351429,2.226300787,2.257560613,2.287132163,2.314861949,2.340608908,2.364249194,2.385681301,2.404831372,2.421658437,2.436159341,2.44837308,2.458384231,2.466325196,2.472376994,2.476768367,2.479773043,2.481705089,2.482912362,2.483768183,2.484661476,2.485985696,2.488126968,2.491451952,2.49629594,2.502951767,2.511660057,2.522601279,2.535890021,2.551571767,2.569622329,2.589949966,2.612400051,2.636762045,2.662778389,2.690154851,2.718571797,2.747695813,2.777191115,2.806730228,2.836003454,2.864726763,2.892647839,2.919550123,2.94525482,2.969620968,2.992543759,3.013951403,3.033800919,3.052073264,3.068768271,3.083899869,3.09749201,3.109575734,3.120187681,3.129370277,3.137173718,3.14365971,3.148906776,3.153016814,3.156122394,3.158394209,3.160047959,3.16134991,3.162620384,3.164234496,3.166619568,3.170248891,3.175631696,3.183299513,3.193789375,3.207624618,3.225294261,3.247232132,3.273797024,3.305255147,3.34176603,3.383372882,3.429998064,3.481444053,3.537399869,3.597452579,3.66110317,3.727785794,3.79688922,3.867779224,3.939820672,4.012398145,4.084934144,4.15690414,4.227848006,4.297377656,4.365180932,4.431022041,4.494738963,4.556238394,4.615488805,4.672512189,4.727375048,4.780179037,4.831051628,4.880137012,4.927587396,4.973554736,5.018182919,5.061600356,5.103912954,5.145197438,5.185495049,5.224805658,5.263082427,5.300227163,5.336086566,5.370449607,5.403046275,5.433547925,5.461569436,5.486673334,5.508375988,5.52615586,5.539463767,5.547734954,5.550402703,5.546913138,5.536740746,5.519404132,5.494481453,5.461624947,5.420574028,5.371166396,5.31334673,5.247172564,5.172817113,5.090568862,5.000827933,4.904099321,4.800983235,4.692162893,4.578390213,4.46046991,4.339242572,4.215567291,4.090304439,3.964299133,3.838365892,3.713274909,3.589740283,3.468410437,3.349860889,3.2345894,3.123013461,3.015469989,2.912217024,2.813437181,2.719242555,2.629680784,2.544741923,2.464365845,2.388449858,2.316856297,2.249419849,2.185954439,2.126259543,2.070125813,2.017339982,1.967688999,1.920963428,1.87696013,1.875484288};
	private static final double RandMin = 1.106541905;
	private static final double RandMax = 5.550402703;

	private static int id_tem = 0;
	private VariableGraph graph_G;
	
	public ServiceGenerator(VariableGraph _graph_G)
	{
		graph_G = _graph_G;
		//rs 资源已经在里面了 每条边上有对应的资源描述
	}
	
	public void updateGraph(VariableGraph _graph_G)
	{
		graph_G = _graph_G;
	}
	
	public PacketService create_PacketService(int _static_bw){
		
		int a,b,tem,_priority;

		a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		while(a==b)
		{
			a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		}
		if(a > b)
		{
			tem = a;
			a = b;
			b= tem;
		}
		
		tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(1,100);
		if(tem<50)
			_priority = 1;
		else if(tem>=50 && tem <=90)
			_priority = 2;
		else
			_priority = 3;
		
		return new PacketService(generate_an_id(),a,b,_priority,_static_bw,0);
	}
	
	public PacketService create_PacketService(int _sourceVertex, int _sinkVertex, int _static_bw){
		
		int tem,_priority;

		if(_sourceVertex == _sinkVertex)
		{
			System.out.println("With same source and sink address in OpticalService.java");
		}
		else if(_sourceVertex > _sinkVertex)
		{
			tem = _sourceVertex;
			_sourceVertex = _sinkVertex;
			_sinkVertex= tem;
		}
		
		tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(1,100);
		if(tem<60)
			_priority = 1;
		else if(tem>=60 && tem <=85)
			_priority = 2;
		else
			_priority = 3;
		
		return new PacketService(generate_an_id(),_sourceVertex,_sinkVertex,_priority, _static_bw,0);
	}
	
	public List<PacketService> random_PacketServices(int packetService_num, int bw_type, int last_time_type)
	{
		List<PacketService> ps_list = new ArrayList<PacketService>();
		
		int _souce_node, _dest_node, _priority, tem;
		PacketService ps_tem = null;
		for(int i = 0; i<packetService_num; i++)
		{
			_souce_node = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			_dest_node = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			while(_souce_node ==_dest_node)
			{
				_souce_node = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
				_dest_node = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			}
			if(_souce_node > _dest_node)
			{
				tem = _souce_node;
				_souce_node = _dest_node;
				_dest_node= tem;
			}
			
			_priority = generatePriority();
			if(ServiceGenerator.SERVICE_WITH_RANDOM_BW == bw_type && ServiceGenerator.PERMENENT_SERVICE == last_time_type)
				ps_tem = new PacketService(generate_an_id(),_souce_node,_dest_node,_priority,PacketService.RANDOM_BW,0);
			else
				System.out.println("Wait to be completed!");
			
			ps_list.add(ps_tem);
		}		
		return ps_list;
	}
	
	public int generatePriority()
	{
		int tem;
		tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(1,100);
		if(tem<40)
			return Service.PRIORITY_HIGH;
		else if(tem>=40 && tem <=80)
			return Service.PRIORITY_MID;
		else
			return Service.PRIORITY_LOW;
	}
	
/*	public List<PacketService> random_PacketServices(int random_num,int _static_bw,boolean last_forever_f){
		//
		List<PacketService> ps_list = new Vector<PacketService>();
		int a,b,tem,_priority;
		for(int i = 0; i < random_num; i++)
		{
			
			if(PacketService.PERMANENT == last_forever_f)
				ps_list.add(new PacketService(generate_an_id(),a,b,_priority,_static_bw,0));
			else
			{
				int timeLong_tem = (int)(P_rand(5)+1)*LASTTIME_BASE_NUM;
				ps_list.add(new PacketService(generate_an_id(),a,b,_priority,_static_bw,timeLong_tem));
			}
		}
		return ps_list;		
	}*/
	
	public OpticalService create_OpticalService(){
		
		int a,b,tem;

		a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		while(a==b)
		{
			a = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
			b = java.util.concurrent.ThreadLocalRandom.current().nextInt(graph_G.get_vertex_num());
		}
		if(a > b)
		{
			tem = a;
			a = b;
			b= tem;
		}
		return new OpticalService(generate_an_id(),a,b,OpticalService.SINGLE_SLOT,OpticalService.INIT_TIME_LONG);
	}
	
	public OpticalService create_OpticalService(int _sourceVertex, int _sinkVertex){
		
		int tem;

		if(_sourceVertex == _sinkVertex)
		{
			System.out.println("With same source and sink address in OpticalService.java");
		}
		else if(_sourceVertex > _sinkVertex)
		{
			tem = _sourceVertex;
			_sourceVertex = _sinkVertex;
			_sinkVertex= tem;
		}
		return new OpticalService(generate_an_id(),_sourceVertex,_sinkVertex,OpticalService.SINGLE_SLOT,OpticalService.INIT_TIME_LONG);
	}
	
	public static double P_rand(double Lamda)
	{// 泊松分布
		double x=0,b=1,c=Math.exp(-Lamda),u; 
		do {
			u=Math.random();
			b *=u;
			if(b>=c)
				x++;
		}while(b>=c);
		return x;
	}
	
	public static int generate_an_id()
	{
		return id_tem++;
	}
		
	public static int[] generate_bw_buckets(int priority) {
		if(RandomArry.length != Service.TIME_BUCKET_NUM)
		{
			System.out.println("Big mistake!!!");
			return null;
		}
		
		int x = createRandomStartPlace();
		double ratio = createRandomRatioOfcrestToTrough();
		int basicBw = createRandomBasicBW(priority);
		double multiple_p = (ratio-1)*basicBw/(RandMax-RandMin);
		if(multiple_p<0)
			System.out.println("Something wrong:"+multiple_p);
		basicBw = basicBw- (int)(multiple_p*RandMin);
		int []real_time_bw = new int[PacketService.TIME_BUCKET_NUM];
		for(int i =0;i<PacketService.TIME_BUCKET_NUM;i++)
		{
			real_time_bw[i] = (int)(basicBw+multiple_p*RandomArry[x]);
			x++;
			if(x == PacketService.TIME_BUCKET_NUM)
				x = 0;
			//System.out.print(real_time_bw[i]+"\t");
		}
		return real_time_bw;
	}
	
	private static int createRandomBasicBW(int priority)
	{
		int _tem = 0;
		
		if(priority == Service.PRIORITY_HIGH)
			_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(100,300);
		else if(priority == Service.PRIORITY_MID)
			_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(200,400);
		else if(priority == Service.PRIORITY_LOW)
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
	
	private static double createRandomRatioOfcrestToTrough()
	{
		double _tem = java.util.concurrent.ThreadLocalRandom.current().nextDouble(1.1,4.5);
		return _tem;
	}
	
	private static int createRandomStartPlace()
	{
		int _tem;
		int _tem_a = java.util.concurrent.ThreadLocalRandom.current().nextInt(2);
		if(_tem_a == 0)
			_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(RandomArry.length);
		else
		{
			_tem = java.util.concurrent.ThreadLocalRandom.current().nextInt(RandomArry.length/6)-24;
			if(_tem < 0)
				_tem+=RandomArry.length;
		}
		return _tem;
	}
}
