package edu.bupt.ipoc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.handler.OTNRequestHandler;
import edu.bupt.ipoc.handler.OpticalResourceRequestHandler;
import edu.bupt.ipoc.handler.PacketServiceRequestHandler;
import edu.bupt.ipoc.handler.VTLRequestHandler;
import edu.bupt.ipoc.managers.EventManager;
import edu.bupt.ipoc.managers.OTNServiceManager;
import edu.bupt.ipoc.managers.OpticalServiceManager;
import edu.bupt.ipoc.managers.PacketServiceManager;
import edu.bupt.ipoc.managers.VTLManager;
import edu.bupt.ipoc.service.BandwidthTolerantPacketService;
import edu.bupt.ipoc.service.OTNService;
import edu.bupt.ipoc.service.OpticalService;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.SubBTService;
import edu.bupt.ipoc.service.ServiceGenerator;
import edu.bupt.ipoc.service.VirtualTransLink;
import edu.bupt.ipoc.tools.SimpleStatisticTool;

public class CentralizedController implements BasicController {

	public boolean f_debug_info = false;
	
	private VariableGraph graph_G = null;
	
	public PacketServiceManager psm = null;
	public VTLManager vtlm = null;
	public OpticalServiceManager osm = null;
	public OTNServiceManager otnsm = null;
	public EventManager em = null;
	
	public PacketServiceRequestHandler psrh = null;
	public VTLRequestHandler vtlrh = null;
	public OpticalResourceRequestHandler orrh = null;
	public OTNRequestHandler otnrh = null;
	
	public SimpleStatisticTool sst = null;
		
	public CentralizedController(VariableGraph _graph_G)
	{
		this.graph_G = _graph_G;
		psm = new PacketServiceManager(this);
		vtlm = new VTLManager(this);
		osm = new OpticalServiceManager(this);
		otnsm = new OTNServiceManager(this);
		em = new EventManager(this);
		
		psrh = new PacketServiceRequestHandler(this);
		vtlrh = new VTLRequestHandler(this);
		orrh = new OpticalResourceRequestHandler(_graph_G, this);
		otnrh = new OTNRequestHandler(this);
		sst = new SimpleStatisticTool(this);
	}
	
	@Override
	public boolean handleServiceRequest(Service ss, int command, Map<Integer,Constraint> cons)
	{
		if(ss instanceof PacketService)
		{	
			PacketService _ps = (PacketService)ss;
			if(psrh.handlerRequest(_ps, command, cons))
			{
				if(command == Service.PS_CARRIED_REQUEST)
					psm.addService(_ps);
				else if(command == Service.PS_REMOVED_REQUEST)
					psm.deleteService(_ps);
				return true;
			}
			else
				sst.addFaultPacketService(_ps);
		}		
		else if(ss instanceof OpticalService)
		{
			OpticalService _os = (OpticalService)ss;
			if(orrh.handlerRequest(_os, command, cons))
			{
				if(command == OpticalService.BUILD_REQUEST)
					osm.addService(_os);
				return true;
			}
		}
		else if(ss instanceof VirtualTransLink)
		{
			VirtualTransLink _vtl = (VirtualTransLink)ss;
			if(vtlrh.handlerRequest(_vtl, command, cons))
			{
				if(command == VirtualTransLink.BUILD_REQUEST)
					vtlm.addService(_vtl);
				return true;
			}
		}
		else if(ss instanceof OTNService)
		{
			OTNService _otns = (OTNService)ss;
			if(otnrh.handlerRequest(_otns, command, cons))
			{
				if(command == Service.OTN_BUILD_REQUEST)
					otnsm.addService(_otns);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Service findExistOneToFitRequest(Service ss, int command, Map<Integer,Constraint> cons) 
	{
		if(ss instanceof PacketService)
		{
			if(command == Service.PS_CARRIED_REQUEST)
			{	
				return vtlm.findFitVTL(cons);
			}
		}
		else if(ss instanceof OTNService)
		{
			OTNService otn = (OTNService) ss;
			return osm.findFitOs(otn.sourceVertex, otn.destVertex, OpticalService.CHANNEL_10G_FOT_OTN, OTNService.OTN_1G);
		}
		System.out.println("Wrong input in findExistOneToFitRequest,check!");
		return null;
	}
	
	/*
	@Override
	public List<Service> findExistOnesToFitRequest(Service ss, int command, Map<Integer,Constraint> cons) {
		
		if(ss instanceof PacketService)
		{
			if(command == Service.PS_CARRIED_REQUEST)
			{
				return (List) vtlm.findFitVTLs(cons);
			}
		}			
		else
			System.out.println("Wrong input in findExistOnesToFitRequest,check!");

		return null;
	}*/

	@Override
	public Service establishNewOneToFitRequest(Service ss, int command, Map<Integer,Constraint> cons) {
		
		if(ss instanceof PacketService)
		{
			PacketService ps = (PacketService) ss;
			
			VirtualTransLink _vtl = new VirtualTransLink();
			_vtl.setID(ServiceGenerator.generate_an_id());
			_vtl.setSourceAndDest(ps.sourceNode, ps.destNode);
			_vtl.setPrioriy(ps.priority);
			
			_vtl.setTypeAccordingToConstraints(cons);
		
			if(this.handleServiceRequest(_vtl, VirtualTransLink.BUILD_REQUEST, cons))
			{
				return _vtl;
			} 		
		}
		else if(ss instanceof VirtualTransLink)
		{
			VirtualTransLink _vtl = (VirtualTransLink) ss;
			
			if((_vtl.vtl_priority == VirtualTransLink.PRIORITY_HIGH || _vtl.vtl_priority == VirtualTransLink.PRIORITY_MID) && command != Service.UseOpticalService )
			{
				OTNService _otn = new OTNService(ServiceGenerator.generate_an_id(),_vtl.sourceVertex, _vtl.destVertex);
				
				if(this.handleServiceRequest(_otn, Service.OTN_BUILD_REQUEST, null))
				{
					return _otn;
				}
			}
			else if(_vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW && command == Service.UseOpticalService )
			{
				OpticalService _os = new OpticalService(ServiceGenerator.generate_an_id(),
						_vtl.sourceVertex, _vtl.destVertex, 1, 0);
				if(this.handleServiceRequest(_os, OpticalService.BUILD_REQUEST, null))
				{
					_os.setType(OpticalService.CHANNEL_10G_SINGLE);
					return _os;
				}			
			}
			else if(_vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW && command == Service.UseOTNService)
			{
				OTNService _otn = new OTNService(ServiceGenerator.generate_an_id(),_vtl.sourceVertex, _vtl.destVertex);
				
				if(this.handleServiceRequest(_otn, Service.OTN_BUILD_REQUEST, null))
				{
					return _otn;
				}
				
			}
		}
		else if(ss instanceof OTNService)
		{
			OTNService _otns = (OTNService) ss;
			
			OpticalService _os = new OpticalService(ServiceGenerator.generate_an_id(),
					_otns.sourceVertex, _otns.destVertex, 1, 0);
			
			if(this.handleServiceRequest(_os, OpticalService.BUILD_REQUEST, null))
			{
				_os.setType(OpticalService.CHANNEL_10G_FOT_OTN);
				return _os;
			}	
		}
		return null;
	}

	@Override
	public List<Service> establishNewOnesToFitRequest(Service ss, int command, Map<Integer,Constraint> cons) {
		
		if(ss instanceof VirtualTransLink)
		{
			int _init_bw_c = -1;
			if(cons.get(Constraint.INITBW_C) != null)
				_init_bw_c = cons.get(Constraint.INITBW_C).value;
			if(_init_bw_c < 0)
			{
				System.out.println("There must be the INITBW_C constraint and the value should be positive!");
				return null;
			}
			
			VirtualTransLink _vtl = (VirtualTransLink) ss;
			
			if(_vtl.type == VirtualTransLink.STATIC_BUT_SHARED || _vtl.type == VirtualTransLink.STATIC_AND_NOT_SHARED)
			{
				_init_bw_c = (int)(_init_bw_c * Service.SURVIVABILITY_FACTOR)+1;
			}
			else if(_vtl.type == VirtualTransLink.VTL_BOD)
			{
				if(_vtl.vtl_priority == VirtualTransLink.PRIORITY_HIGH)
					_init_bw_c = (int)(_init_bw_c/_vtl.th_usp_high);//+1;
				else if(_vtl.vtl_priority == VirtualTransLink.PRIORITY_MID)
					_init_bw_c = (int)(_init_bw_c/_vtl.th_usp_mid);//0+1;
			}

			if(command == VirtualTransLink.BUILD_REQUEST)
			{
				if(_init_bw_c <= OTNService.BW_1G)
				{
					Service _ss = establishNewOneToFitRequest(_vtl,command,cons);
					if(_ss != null)
					{
						List<Service> ssl = new ArrayList<Service>();
						ssl.add(_ss);
						return ssl;
					}				
				}
				else if(OTNService.BW_1G <_init_bw_c && _init_bw_c <= (OpticalService.SUB_OTN_NUM * OTNService.BW_1G))
				{
					//TODO need to be extended, we should use all otns which have the same path, not limited the same os.
					List<OpticalService> _osl = osm.findFitOss(_vtl.sourceVertex,_vtl.destVertex,OpticalService.CHANNEL_10G_FOT_OTN,_init_bw_c);
					//OpticalService _os = osm.findFitOs(_vtl.sourceVertex,_vtl.destVertex,OpticalService.CHANNEL_10G_FOT_OTN,_init_bw_c);
					
					if(_osl !=null)
					{
						return setUpOTNServices(_osl,_init_bw_c);
					}					
					else
					{
						OpticalService _os = new OpticalService(ServiceGenerator.generate_an_id(),
								_vtl.sourceVertex, _vtl.destVertex, 1, 0);
						if(orrh.handlerRequest(_os, OpticalService.BUILD_REQUEST, null))
						{
							_os.setType(OpticalService.CHANNEL_10G_FOT_OTN);
							this.saveService(_os);
							return setUpOTNServices(_os,_init_bw_c);
						}
					}
				}
				else
				{
					System.out.println("Too big bw request");
				}
			}			
			else if(command == VirtualTransLink.EXTEND_REQUEST)
			{
				List<OpticalService> _osl = null;
				
				if(_vtl.vtl_priority != VirtualTransLink.PRIORITY_LOW)
				{
					for(OTNService onts : _vtl.relevantOTNServices)
					{
						OpticalService _current_os = onts.osBelongTo;
						if(_current_os.canOfferEnoughCapacity(_init_bw_c))
						{
							return setUpOTNServices(_current_os,_init_bw_c);					
						}					
					}
					
					//if not success, we should can use otn in optical services which has the same path with the original ones.
					Path _path = _vtl.relevantOTNServices.get(0).osBelongTo.path;
					_osl = osm.findFitOss(_vtl.sourceVertex,_vtl.destVertex,OpticalService.CHANNEL_10G_FOT_OTN,_path);
				}
				else
				{
					_osl = osm.findFitOss(_vtl.sourceVertex,_vtl.destVertex,OpticalService.CHANNEL_10G_FOT_OTN,null);
				}

				List<Service> sl_return = new ArrayList<Service>();
				
				int tem_bw = 0;
				for(OpticalService _os : _osl)
				{
					tem_bw += _os.getNumberofFreeOTNs() * Service.BW_1G;
				}
				
				if(tem_bw >= _init_bw_c)
				{
					for(OpticalService _os : _osl)
					{							
						if(_os.getNumberofFreeOTNs() == 0)
							continue;
						int _bw = _os.getNumberofFreeOTNs() * Service.BW_1G;
						if(_init_bw_c > _bw)
						{
							sl_return.addAll(setUpOTNServices(_os,_bw));								
						}
						else// if(_init_bw_c <= tem_bw)
						{
							sl_return.addAll(setUpOTNServices(_os,_init_bw_c));
						}
						_init_bw_c -= _bw;
						
						if(_init_bw_c < 0)
							break;	
					}
					
					if(_init_bw_c < 0)
					{
						return sl_return;
					}
				}					
				else
				{
					OpticalService _os = new OpticalService(ServiceGenerator.generate_an_id(),
							_vtl.sourceVertex, _vtl.destVertex, 1, 0);
					if(orrh.handlerRequest(_os, OpticalService.BUILD_REQUEST, null))
					{
						_os.setType(OpticalService.CHANNEL_10G_FOT_OTN);
						this.saveService(_os);
						return setUpOTNServices(_os,_init_bw_c);
					}
				}							
			}
			
		}
		return null;
	}
	
	@Override
	public boolean mappingServices(Service ss1, Service ss2, Map<Integer,Constraint> cons) {
		
		if(ss1 instanceof PacketService && ss2 instanceof VirtualTransLink)
		{
			PacketService ps = (PacketService)ss1;
			VirtualTransLink vtl = (VirtualTransLink)ss2;
			
			ps.carriedVTL = vtl;
			vtl.carriedPacketServices.add(ps);			
		}
		else if(ss1 instanceof VirtualTransLink && ss2 instanceof OpticalService)
		{
			VirtualTransLink vtl = (VirtualTransLink)ss1;
			OpticalService os = (OpticalService)ss2;
			
			vtl.addRelevantOpticalService(os);
			
		}
		else if(ss1 instanceof VirtualTransLink && ss2 instanceof OTNService)
		{
			VirtualTransLink vtl = (VirtualTransLink)ss1;
			OTNService otns = (OTNService)ss2;//
			
			vtl.addRelevantOTNService(otns);
			otns.setVTLSupported(vtl);
		}
		else if(ss1 instanceof OTNService && ss2 instanceof OpticalService)
		{
			OTNService otns = (OTNService)ss1;
			OpticalService os = (OpticalService)ss2;
			
			otns.setCarriedOpticalService(os);
			os.addSubOTN(otns);
			//
		}
		else
		{
			System.out.println("There is no map rule for "+ss1.getClass()+"and"+ss2.getClass());
			return false;
		}
		return true;
	}
	

	@Override
	public boolean mappingServices(Service ss, List<Pair<Service, Integer>> _tems, Map<Integer, Constraint> cons) {
		//only for bt service
		if(ss instanceof BandwidthTolerantPacketService)
		{
			BandwidthTolerantPacketService btps = (BandwidthTolerantPacketService)ss;
			if(_tems.get(0).o1 instanceof VirtualTransLink)
			{
				if(_tems != null)
				{
					{
						btps.sub_btpss = new ArrayList<SubBTService>();
						for(Pair<Service, Integer> vtl_bw : _tems)
						{
							SubBTService sub_btps = new SubBTService(ServiceGenerator.generate_an_id(), btps.sourceNode,
									btps.destNode,btps.priority, (VirtualTransLink)vtl_bw.o1, vtl_bw.o2, btps);
							
							this.mappingServices(sub_btps, vtl_bw.o1, null);
							btps.sub_btpss.add(sub_btps);
						}
						btps.updateCurrent_rate();

						//TODO BTPS BW VERIFY
					}					
				}
				return true;
			}
		}
		System.out.println("check the input for mapping services");
		return false;
		
	}
	
	@Override
	public boolean unmappingServices(Service ss, Service _tems, Map<Integer, Constraint> cons) {
		if(ss instanceof PacketService)
		{
			if(_tems instanceof VirtualTransLink)
			{
				((VirtualTransLink) _tems).removeCarriedPacketService((PacketService)ss);
				return true;
			}
		}
		System.out.println("Should never be here!!");
		
		return false;
	}
	
	@Override
	public boolean unmappingServices(Service ss, List<Service> _tems, Map<Integer, Constraint> cons) {
		if(ss instanceof VirtualTransLink)
		{
			VirtualTransLink _vtl = (VirtualTransLink)ss;
			for(Service st : _tems)
			{
				if(st instanceof OpticalService)
				{
					//System.out.println("We are here OpticalService");
					_vtl.removeRelevantOpticalService((OpticalService)st);
				}
				else if(st instanceof OTNService)
				{
					//System.out.println("We are here OTNService");
					OTNService _otn = (OTNService)st;
					_otn.osBelongTo.otn_children.remove(_otn.id);
					_vtl.removeRelevantOTNService(_otn);
				}
			}
			
		}
		return false;
	}

	@Override
	public void saveService(Service ss) {
		
		if(ss instanceof PacketService)
		{
			psm.addService((PacketService)ss);
		}
		else if(ss instanceof OpticalService)
		{
			osm.addService((OpticalService)ss);
		}
		else if(ss instanceof VirtualTransLink)
		{
			vtlm.addService((VirtualTransLink)ss);
		}
		else if(ss instanceof OTNService)
		{
			otnsm.addService((OTNService)ss);
		}
		
	}
	
	public List<Service> setUpOTNServices(OpticalService carriedOS, int requestBW)
	{
		List<Service> ssl = new ArrayList<Service>();
		
		for(;;)
		{
			OTNService _otn = new OTNService(ServiceGenerator.generate_an_id(),carriedOS.sourceVertex, carriedOS.sinkVertex);
			mappingServices(_otn, carriedOS, null);
			requestBW -= OTNService.BW_1G;
			saveService(_otn);
			ssl.add(_otn);
			if(requestBW <= 0)
				break;		
		}
		return ssl;
	}
	
	public List<Service> setUpOTNServices(List<OpticalService> carriedOSs, int requestBW)
	{
		List<Service> ssl = new ArrayList<Service>();
		
		for(OpticalService _carriedOS : carriedOSs)
		{
			for(;;)
			{
				OTNService _otn = new OTNService(ServiceGenerator.generate_an_id(),_carriedOS.sourceVertex, _carriedOS.sinkVertex);
				mappingServices(_otn, _carriedOS, null);
				requestBW -= OTNService.BW_1G;
				saveService(_otn);
				ssl.add(_otn);
				if(requestBW <= 0 || _carriedOS.otn_children.size() == OpticalService.SUB_1G_NUM)
					break;
			}
			if(requestBW<=0)
				break;
		}
		return ssl;
	}

	@Override
	public void clearAll() {
		graph_G.clear_all_resource();
		
		psm.clearAllServices();
		osm.clearAllServices();
		vtlm.clearAllServices();
		otnsm.clearAllServices();		
	}

	public void checkVTLStatue() {
		vtlm.checkVTLStatue();				
	}
	
	public void checkBTPSStatue()
	{
		
	}

	@Override
	public int getAvailableServiceNumber(int _source, int _dest, int _type) {
		if(_type == Service.TYPE_OTN)
		{
			return osm.getAvailableOTNsNumber(_source, _dest);
		}
		return 0;
	}


	@Override
	public List<Pair<Service, Integer>> getOnesWithBwValueToFitRequest(Service ss, int command,
			Map<Integer, Constraint> cons) {
		if(ss instanceof BandwidthTolerantPacketService)
		{
			BandwidthTolerantPacketService btps = (BandwidthTolerantPacketService)ss;
			int tem_bw = cons.get(Constraint.INITBW_C).value;
			List<Pair<Service,Integer>> target_vtls;
			
			cons.get(Constraint.INITBW_C).value = (int)(Service.MAX_ALLOWED_BW * VirtualTransLink.TH_USP_LOW);
			target_vtls = vtlm.findFitVTLsWithBwValue(cons);
			
			if(target_vtls != null && target_vtls.size()>0)
				return target_vtls;
			
			cons.get(Constraint.INITBW_C).value = tem_bw;
			target_vtls = new ArrayList<Pair<Service,Integer>>();
			VirtualTransLink tem_vtl = vtlm.findAnExtendedableVTL(cons);
			
			if(tem_vtl != null)
			{
				if(vtlrh.handlerRequest(tem_vtl, VirtualTransLink.EXTEND_REQUEST, null))//EXTEND 10G
				{
					target_vtls.add(new Pair(tem_vtl,(int)(Service.MAX_ALLOWED_BW * VirtualTransLink.TH_USP_LOW)));
					return target_vtls;
				}
			}
			else
			{
				VirtualTransLink _vtl = new VirtualTransLink();
				_vtl.setID(ServiceGenerator.generate_an_id());
				_vtl.setSourceAndDest(btps.sourceNode, btps.destNode);
				_vtl.setPrioriy(btps.priority);
				_vtl.setTypeAccordingToConstraints(cons);
				
				Map<Integer,Constraint> _cons = new HashMap<Integer,Constraint>();
				
				_cons.put(Constraint.VTL_PREFER_RESOURCE_C, 
						new Constraint(Constraint.VTL_PREFER_RESOURCE_C,Service.UseOpticalService, "The vtl prefer use optical service"));
				
				if(handleServiceRequest(_vtl, VirtualTransLink.BUILD_REQUEST, _cons))
				{
					tem_vtl = _vtl;
					target_vtls.add(new Pair(tem_vtl,(int)(Service.MAX_ALLOWED_BW * VirtualTransLink.TH_USP_LOW)));
					return target_vtls;
				}
			}
			
			int canOfferBW = 0;
			int tem = 0;
			
			List<VirtualTransLink> _vtll = vtlm.getVTLs(btps.sourceNode, btps.destNode);
			if(_vtll != null && _vtll.size() > 0)
			{
				for(VirtualTransLink vtl : _vtll)
				{
					if(vtl.type == VirtualTransLink.VTL_BOD)
					{
						tem = vtl.getAcutallyRestBWforShare();//getAcutallyRestBW();
						if(tem > 0)
						{
							canOfferBW += tem;
							target_vtls.add(new Pair(vtl,tem));
						}
						else if(tem<0)
						{
							System.out.println("0There must be some big mistake!! the canOfferBW is:"+tem);
							tem = vtl.getAcutallyRestBWforShare();
						}
					}
				}
			}
			else
				System.out.println("Why");
			
			if(canOfferBW >= (Service.MAX_ALLOWED_BW * VirtualTransLink.TH_USP_LOW))
			{
				//TODO later. sort the vtls.
				System.out.println("There is no way to be here");
				return target_vtls;
			}
			else
			{
				int _rest_need_bw_max = (int)(Service.MAX_ALLOWED_BW * VirtualTransLink.TH_USP_LOW) - canOfferBW;		
				int availble_otn_number = 0;
				int minAllRequestBW = cons.get(Constraint.INITBW_C).value;		
				
				availble_otn_number = osm.getAvailableOTNsNumber(btps.sourceNode, btps.destNode);//System.out.println("otn_1G_number"+otn_1G_number);

				int bw_can_be_requested = 0;
				if((int)(availble_otn_number * Service.BW_1G * VirtualTransLink.TH_USP_LOW) >= _rest_need_bw_max)
					bw_can_be_requested = _rest_need_bw_max;
				else
				{
					bw_can_be_requested = (int)(availble_otn_number * Service.BW_1G * VirtualTransLink.TH_USP_LOW);
					if((int)(availble_otn_number * Service.BW_1G * VirtualTransLink.TH_USP_LOW) + canOfferBW < minAllRequestBW )
					{
						int differ_value = minAllRequestBW - canOfferBW - (int)(availble_otn_number * Service.BW_1G * VirtualTransLink.TH_USP_LOW);
						List<Pair<Service,Integer>> tt= psm.shrinkedPSforMoreBW(btps.sourceNode, btps.destNode, differ_value);
						if(tt == null || tt.size() <=0)
							return null;
						target_vtls.addAll(tt);
						if(availble_otn_number == 0)
							return target_vtls;
					}
					else
					{
						if(availble_otn_number > 0)
						{
							if(tem_vtl != null)
							{
								Map<Integer,Constraint> _cons = new HashMap<Integer,Constraint>();
								Constraint _con = new Constraint(Constraint.INITBW_C,bw_can_be_requested,"Cons bw is"+bw_can_be_requested);
								_cons.put(Constraint.INITBW_C, _con);
								
								if(handleServiceRequest(tem_vtl, VirtualTransLink.EXTEND_REQUEST,_cons))
								{
									target_vtls.add(new Pair(tem_vtl,bw_can_be_requested));
									return target_vtls;
								}
								else
								{
									System.out.println("bw_can_be_requested"+bw_can_be_requested);
									System.out.println("This should not be happened!");
									return null;
								}
							}
							else
							{
								VirtualTransLink _vtl = new VirtualTransLink();
								_vtl.setID(ServiceGenerator.generate_an_id());
								_vtl.setSourceAndDest(btps.sourceNode, btps.destNode);
								_vtl.setPrioriy(btps.priority);
								_vtl.setTypeAccordingToConstraints(cons);
								
								Map<Integer,Constraint> _cons = new HashMap<Integer,Constraint>();
								
								_cons.put(Constraint.VTL_PREFER_RESOURCE_C, 
										new Constraint(Constraint.VTL_PREFER_RESOURCE_C,Service.UseOTNService, "The vtl prefer use OTN service"));
								
								if(handleServiceRequest(_vtl, VirtualTransLink.BUILD_REQUEST, _cons))
								{
									tem_vtl = _vtl;
									target_vtls.add(new Pair(tem_vtl,(int)(Service.BW_1G * VirtualTransLink.TH_USP_LOW)));
								}
								if(tem_vtl == null)
									System.out.println("Should not be here");					
								
								availble_otn_number--;
								if(availble_otn_number > 0)
								{
									bw_can_be_requested = (int)((availble_otn_number * Service.BW_1G * VirtualTransLink.TH_USP_LOW));
									_cons.clear();
									_cons = new HashMap<Integer,Constraint>();
									Constraint _con = new Constraint(Constraint.INITBW_C,bw_can_be_requested,"Cons bw is"+bw_can_be_requested);
									_cons.put(Constraint.INITBW_C, _con);					
									if(handleServiceRequest(tem_vtl, VirtualTransLink.EXTEND_REQUEST,_cons))
									{
										target_vtls.add(new Pair(tem_vtl,bw_can_be_requested));
									}
								}
								return target_vtls;
							}
						}
						else
						{
							return target_vtls;
						}
					}
				}
				
				
			}			
		}
		// TODO Auto-generated method stub
		System.out.println("Bad input in getOnesWithBwValueToFitRequest");
		return null;
	}

	@Override
	public Service getOneToFitRequest(Service ss, int command, Map<Integer, Constraint> cons) 
	{
		if(ss instanceof PacketService)
		{
			PacketService ps = (PacketService)ss;
			if(ps.priority == Service.PRIORITY_LOW)
			{
				//TODO LATER
				return null;
			}
			Service _tem;
			_tem = findExistOneToFitRequest(ps,command, cons);
			if(_tem != null)
			{
				return _tem;
			}
			else
			{
				_tem = establishNewOneToFitRequest(ps, command, cons);
				if(_tem != null)
				{
					return _tem;
				}
			}
		}
		return null;
	}
	
	public boolean adjustSpecificVTL(VirtualTransLink _vtl, int _adjust_bw)
	{
		VirtualTransLink tem_vtl;
		Map<Integer, Constraint> cons;
		
		List<PacketService> tem_pss = _vtl.getServiceWithLowPriority();
		int request_bw = _adjust_bw;
		
		if(tem_pss == null || tem_pss.size() == 0)
			return true;
		
		if(request_bw > 0)
		{
			Collections.sort(tem_pss);
			SubBTService sub_s;
			int canOfferBW = 0;
			
			List<SubBTService> target_ss = new ArrayList<SubBTService>();
			
			for(PacketService t_s : tem_pss)
			{
				sub_s = (SubBTService)t_s;
				canOfferBW += sub_s.bwCanShrinked();
				target_ss.add(sub_s);
				if(canOfferBW >= request_bw)
					break;			
			}
			
			if(canOfferBW >= request_bw)
			{
				for(SubBTService _t_sub_s : target_ss)
				{
					canOfferBW = _t_sub_s.bwCanShrinked();
					if(canOfferBW > request_bw)
						_t_sub_s.shrinkItself(request_bw);
					else
					{
						_t_sub_s.shrinkItself(canOfferBW);
					}
						
				}
			}
		}
		else if(request_bw < 0)
		{
			//TODO
			//TODO
			//TODO
			//sort the subService according the rest_time_long
			Collections.reverse(tem_pss);
			SubBTService sub_s;
			int canUseMoreBW = -request_bw;
			int tem = 0;
			
			for(PacketService t_s : tem_pss)
			{
				sub_s = (SubBTService)t_s;
				
				tem = sub_s.bwCanExpended();
				if(canUseMoreBW > tem)
				{
					sub_s.expendItself(tem);
					canUseMoreBW -= tem;
				}
				else
				{
					sub_s.expendItself(canUseMoreBW);
					break;
				}
				
		
			}
			//
			//if tem = _t_sub_s.bwCanExpend
			// canExpendBW > tem
			// _t_sub_s.expend
			// canExpendBW < tem
			// _t_sub_s.expend canExpendBW
			// canExpendBw-=
		}
		else
		{
			System.out.println("Should not be here, bigbug");
			return false;
		}
		return true;
	}

	@Override
	public boolean debugInfo() {
		// TODO Auto-generated method stub
		return f_debug_info;
	}


}
