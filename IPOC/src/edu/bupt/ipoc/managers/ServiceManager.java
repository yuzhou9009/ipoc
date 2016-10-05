package edu.bupt.ipoc.managers;

import edu.bupt.ipoc.service.Service;

public interface ServiceManager {

	
	public boolean addService(Service ss);
	
	public boolean deleteService(Service ss);
	
	/*
	
	public static final boolean CREAT_NEW_OPTIC_CHANNEL_IF_NEED = true;
	public static final boolean WITHOUT_CREAT_NEW_OPTIC_CHANNEL = false;
	
	public static final boolean USE_LINK_AGGREGATION = true;
	public static final boolean NO_LINK_AGGREGATION = false;
	
	public static final boolean ALLOCATE_WITH_MAX_BW = true;
	public static final boolean ALLOCATE_WITH_CURRENT_BW = false;
	

	
	public static final int SUPPORT_WITH_AGGREGATION = 2;
	public static final int SUPPORT_WITHOUT_AGGREGATION = 1;
	public static final int NOT_SUPPORT = 0;
	
	//request type: allocate resource or release resource for packet service 
	public static final int ALLOCATE_RESOURCE = 1;
	public static final int RELEASE_RESOURCE = 2;
	
	public static final long MAX_FREE_TIME_LONG = 12000;
	
	public static final int NORMAL_COST_TIME = 20;
	public static final int LESS_COST_TIME = 5;
	
	public int total_packset_num = 0;
	public double average_time = 0.0;
	
	public int new_count = 0;
	
	
	public boolean Link_aggregation_f = USE_LINK_AGGREGATION;//NO_LINK_AGGREGATION;
	
	private VariableGraph graph_G;// = new VariableGraph("data/cost239");
	private OpticalResourceManager orm;
	private VTLManager vtlm;
	
	public Timer _timer = new Timer();
	
	protected Map<Pair<Integer, Integer>, List<OpticalService>> _vertex_pair_os_list_index = 
			new HashMap<Pair<Integer,Integer>, List<OpticalService>>();
	private List<OpticalService> os_list;
	private List<PacketService> ps_list;
		
	public ServiceManager(VariableGraph _graph_G, VTLManager _vtlm, OpticalResourceManager _orm)
	{
		graph_G = _graph_G;
		vtlm = _vtlm;
		this.orm = _orm;
		this.Init_PacketServices();
		this.Init_OpticalServices();

		//rs 资源已经在里面了 每条边上有对应的资源描述
	}


	public void UpdateGraph(VariableGraph _graph_G)
	{
		graph_G = _graph_G;
	}
	
	
	public boolean Init_PacketServices(){
		//
		ps_list = new Vector<PacketService>();
		return true;		
	}
		
	public boolean Init_OpticalServices(){
		
		os_list = new Vector<OpticalService>();
		
		return true;
	}
	
	public boolean add_PacketService(PacketService _ps)
	{
		ps_list.add(_ps);
		return true;
	}
	
	public boolean add_OpticalService(OpticalService _os)
	{
		Pair<Integer, Integer> pair_tem = new Pair<Integer, Integer>(_os.sourceVertex,_os.sinkVertex);
		os_list.add(_os);
		if(_vertex_pair_os_list_index.containsKey(pair_tem))
		{
			_vertex_pair_os_list_index.get(pair_tem).add(_os);
		}
		else
		{
			List<OpticalService> _osl;
			_osl = new Vector<OpticalService>();
			_osl.add(_os);
			_vertex_pair_os_list_index.put(pair_tem,_osl);	
		}
		return true;
	}
	
	public boolean remove_OpticalService(OpticalService _os)
	{
		Pair<Integer, Integer> pair_tem = new Pair<Integer, Integer>(_os.sourceVertex,_os.sinkVertex);
		os_list.remove(_os);
		if(_vertex_pair_os_list_index.containsKey(pair_tem))
		{
			_vertex_pair_os_list_index.get(pair_tem).remove(_os);
		}
		return true;
	}
	
	public boolean removeAllOpticalService()
	{
		_vertex_pair_os_list_index.clear();
		os_list.clear();
		return true;
	}
	
	public boolean find_and_allocate_ps(PacketService _ps)
	{
		return true;
	}
	
	public synchronized boolean dealWithPacketService(
			PacketService _ps, 
			boolean create_new_optic_channel_flag, 
			boolean _share_channel_flag,
			boolean _static_flag,
			int request_type)
	{
		
		if(request_type == ALLOCATE_RESOURCE)
			return allocatePacketService(_ps, create_new_optic_channel_flag, _share_channel_flag, _static_flag);
		else
			return this.movePacketServiceFromOptical(_ps);
			

	}
	

	public synchronized boolean allocatePacketService(PacketService _ps, 
			boolean create_new_optic_channel_flag, 
			boolean _share_channel_flag,
			boolean _static_flag) {
		
		int support_flag = isNetworkSupportPsBw(_ps,_static_flag);

		if(support_flag != NOT_SUPPORT)
		{
			if(putInSuitableOpticalService(_ps,_share_channel_flag,_static_flag))
			{
				double _total = 0;
				_total = average_time * total_packset_num + LESS_COST_TIME;
				total_packset_num ++;
				average_time = _total/total_packset_num;
				return true;
			}
			else
			{
				OpticalService _os_tem = null;
				new_count++;
				//int ps_tem_bw = _ps.getStaticBw();					
				//if(ps_tem_bw> OpticalService.BW_40G && ps_tem_bw<=OpticalService.BW_100G)
				//{}
				//else if(ps_tem_bw> OpticalService.BW_10G && ps_tem_bw<=OpticalService.BW_40G)
				//{}
				//else if(ps_tem_bw> OpticalService.BW_1G && ps_tem_bw<=OpticalService.BW_10G)
				//{}
				//else if( ps_tem_bw<=OpticalService.BW_1G)
				//{}
				_os_tem = new OpticalService10G(
						ServiceGenerator.generate_an_id(),
						_ps.sourceVertex, _ps.sinkVertex,
						OpticalService.SINGLE_SLOT,
						OpticalService.INIT_TIME_LONG,
						OpticalService.CHANNEL_10G_WITH_TEN_1G);
				if(_os_tem!=null)
				{
					//System.out.println("The static_bw:"+_ps.static_bw+"\tCapacity:"+ _os_tem.getInitCapacity()+"\tps_id:"+_ps.id);						
					if(orm.handle_traffic(_os_tem, OpticalResourceManager.BUILD, OpticalResourceManager.FIRST_FIT))
					{
						add_OpticalService(_os_tem);
						if(SUPPORT_WITHOUT_AGGREGATION == support_flag)
							return catchSuitableOpticalServiceAndAllocateIntoIt(_ps,_os_tem, _share_channel_flag,_static_flag);
						else if(SUPPORT_WITH_AGGREGATION == support_flag)
						if(putInSuitableOpticalService(_ps,_share_channel_flag,_static_flag))
						{
							double _total = 0;
							_total = average_time * total_packset_num + NORMAL_COST_TIME;
							total_packset_num ++;
							average_time = _total/total_packset_num;
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	public int isNetworkSupportPsBw(PacketService _ps, boolean static_flag)
	{
		int ps_tem_bw = 0;	
		if(ALLOCATE_WITH_MAX_BW == static_flag)
			ps_tem_bw = _ps.static_bw;			
		else
			ps_tem_bw = _ps.real_time_bw[_ps.bucket_count];
		
		if(NO_LINK_AGGREGATION == Link_aggregation_f )
		{
			if(ps_tem_bw <= OpticalService.BW_1G)	
				return SUPPORT_WITHOUT_AGGREGATION;
		}
		else if(USE_LINK_AGGREGATION == Link_aggregation_f)
		{
			if(ps_tem_bw <= OpticalService.BW_1G)	
				return SUPPORT_WITHOUT_AGGREGATION;
			else
				return SUPPORT_WITH_AGGREGATION;
		}
		return NOT_SUPPORT;
	}
	
	
	public synchronized boolean allocatePacketServiceDynamiclly(PacketService _ps, boolean create_new_optic_channel_flag) {
		
		if(putInSuitableOpticalService(_ps,OpticalService.SHARE_CHANNLE,ALLOCATE_WITH_CURRENT_BW))
		{
			return true;
		}
		else
		{
			if(CREAT_NEW_OPTIC_CHANNEL == create_new_optic_channel_flag)
			{
				//System.out.println("in here");
				OpticalService _os_tem = new OpticalService(ServiceGenerator.generate_an_id(),_ps.sourceVertex,_ps.sinkVertex,OpticalService.INIT_RATE,OpticalService.INIT_TIME_LONG);
				int tem_bw = (_ps.static_bw == 0)?_ps.real_time_bw[_ps.bucket_count]:_ps.static_bw;
				
				if(tem_bw <=OpticalService.BW_10G)
				{
					_os_tem.type = OpticalService.WITH_TEN_10G;
					_os_tem.child10G = new OpticalService[OpticalService.SUB_10G_NUM];
					for(int i = 0;i<OpticalService.SUB_10G_NUM;i++)
					{
						_os_tem.child10G[i] = new OpticalService(ServiceGenerator.generate_an_id(),OpticalService.BW_10G);
					}
				}
				else if(tem_bw > OpticalService.BW_10G &&tem_bw <=OpticalService.BW_40G)
				{
					//System.out.println("ps_id:"+_ps.id+"40G");
					_os_tem.type = OpticalService.WITH_TWO_40G;
					_os_tem.child40G = new OpticalService[OpticalService.SUB_40G_NUM];
					for(int i = 0;i<OpticalService.SUB_40G_NUM;i++)
					{
						_os_tem.child40G[i] = new OpticalService(ServiceGenerator.generate_an_id(),OpticalService.BW_40G);
					}
				}
				else if(tem_bw> OpticalService.BW_40G &&tem_bw <=OpticalService.BW_100G)
				{
					_os_tem.type = OpticalService.SINGLE_100G;
				}
				
				if(rm.handle_traffic(_os_tem, ResourceManager.BUILD, ResourceManager.FIRST_FIT))
				{
					add_OpticalService(_os_tem);
					return putInSuitableOpticalService(_ps,OpticalService.SHARE_CHANNLE);
				}
				
			}
		}
		return false;
	}

	private boolean putInSuitableOpticalService(PacketService _ps,boolean _share_flag, boolean _static_bw_f)
	{
		Pair<Integer, Integer> pair_tem = new Pair<Integer, Integer>(_ps.sourceVertex,_ps.sinkVertex);
		List<OpticalService> _oss = _vertex_pair_os_list_index.get(pair_tem);
		if(_oss == null || _oss.isEmpty())
		{
			return false;
		}
		else
		{
			int support_flag = isNetworkSupportPsBw(_ps,_static_bw_f);
			if(SUPPORT_WITHOUT_AGGREGATION == support_flag)
			{
				for(OpticalService _os : _oss)
				{
					//System.out.println("We got a same source and sink traffic");
					if(catchSuitableOpticalServiceAndAllocateIntoIt(_ps,_os, _share_flag, _static_bw_f))
					{
						//System.out.println("we are in the putInSuitableOpticalService,and success!");
						return true;
					}
				}				
			}
			else if(SUPPORT_WITH_AGGREGATION == support_flag)
			{
				if(USE_LINK_AGGREGATION == Link_aggregation_f)
				{
					//System.out.println("we are in the putInSuitableOpticalService,USE_LINK_AGGREGATION");
					if(catchSuitableOpticalServicesAndAllocateIntoThem(_ps,_oss, _share_flag, _static_bw_f))
					{
						return true;
					}
					//_ps.breakIntoSetOfChildren(_static_bw_f);
					//for(PacketService _psc : _ps.ps_children.values())						
				}
			}			
			return false;
		}
	}
	

	private boolean catchSuitableOpticalServiceAndAllocateIntoIt(PacketService _ps, OpticalService _os,boolean _share_flag, boolean _static_bw_f)
	{
		//System.out.println("we are in the catch");
		return _os.carryPacketService(_ps,_share_flag,Link_aggregation_f,_static_bw_f,true);

	}
	
	private boolean catchSuitableOpticalServiceButNotAllocate(PacketService _ps, OpticalService _os,boolean _share_flag, boolean _static_bw_f)
	{
		//System.out.println("we are in the catch");
		return _os.carryPacketService(_ps,_share_flag,Link_aggregation_f,_static_bw_f,false);

	}
	
	private boolean catchSuitableOpticalServicesAndAllocateIntoThem(PacketService _ps, List<OpticalService> _oss,boolean _share_flag, boolean _static_bw_f)
	{
		for(OpticalService _os : _oss)
		{
			//System.out.println("we are in the catchSuitableOpticalServicesAndAllocateIntoThem!");
			if(_os.carryPacketServiceByLinkAggregation(_ps,_share_flag,_static_bw_f,true))
			{
				//System.out.println("we are in the putInSuitableOpticalService,and success!");
				return true;
			}
			else
			{
				
			}
		}
		return false;
	}

	
	public Map<Pair<Integer, Integer>, List<OpticalService>> get_vertex_pair_os_list_index() {
		return _vertex_pair_os_list_index;
	}

	public List<OpticalService> getOs_list() {
		return os_list;
	}

	public List<PacketService> getPs_list() {
		return ps_list;
	}

	public boolean movePacketServiceFromOptical(PacketService _ps)
	{
		//System.out.println("SM:"+this);
		return _ps.removeMyself();
		//
	}

	public DelePacketServiceTask set_deletrafic_task(PacketService _ps)
	{
		DelePacketServiceTask del_trf = new DelePacketServiceTask(_ps);
		return del_trf;
	}
	
	class DelePacketServiceTask extends TimerTask
	{
		PacketService _task_ps;
		public DelePacketServiceTask(PacketService _ps)
		{
			_task_ps = _ps;
		}

		public void run() {
			//System.out.println("test");
			dealWithPacketService(_task_ps,false,false,false,RELEASE_RESOURCE);
		}
		
	}
	
	public boolean reAllocatePacketService(PacketService _max_ps,
			OpticalService os_tem) {
		// TODO Auto-generated method stub
		return false;
		
	}*/
}
