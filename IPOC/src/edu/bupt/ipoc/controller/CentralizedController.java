package edu.bupt.ipoc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import edu.bupt.ipoc.service.OTNService;
import edu.bupt.ipoc.service.OpticalService;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.ServiceGenerator;
import edu.bupt.ipoc.service.VirtualTransLink;
import edu.bupt.ipoc.tools.SimpleStatisticTool;

public class CentralizedController implements BasicController {

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
	}

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
			else if(_vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW || command == Service.UseOpticalService )
			{
				OpticalService _os = new OpticalService(ServiceGenerator.generate_an_id(),
						_vtl.sourceVertex, _vtl.destVertex, 1, 0);
				if(this.handleServiceRequest(_os, OpticalService.BUILD_REQUEST, null))
				{
					_os.setType(OpticalService.CHANNEL_10G_SINGLE);
					return _os;
				}			
			}
			else if(_vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW || command == Service.UseOTNService)
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
					_init_bw_c = (int)(_init_bw_c/_vtl.th_usp_high)+1;
				else if(_vtl.vtl_priority == VirtualTransLink.PRIORITY_MID)
					_init_bw_c = (int)(_init_bw_c/_vtl.th_usp_mid)+1;
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
				if(_vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW)
				{
					//TODO
				}
				else
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
					
					List<OpticalService> _osl = osm.findFitOss(_vtl.sourceVertex,_vtl.destVertex,OpticalService.CHANNEL_10G_FOT_OTN,_path);
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
	public boolean mappingServices(Service ss, List<Service> _tems, Map<Integer, Constraint> cons) {
		if(ss instanceof PacketService)
		{
			PacketService ps = (PacketService)ss;
			if(_tems.get(0) instanceof VirtualTransLink)
			{
				List<VirtualTransLink> vtls = (List)_tems;
				if(vtls != null)
				{
					{
						//test
						int _arbw = ps.getCurrentOccupiedBw();
						int bww = 0;
						for(VirtualTransLink vsss : vtls)
						{
							bww += vsss.getAcutallyRestBWforShare();//getAcutallyRestBW();
						}
						if(bww<_arbw)
						{
							System.out.println("**************big problem£¬the different value is"+ (bww-_arbw));
							
							int _total_need_bw = ps.getCurrentOccupiedBw();
							System.out.println("Total need bw is"+_total_need_bw);
							for(VirtualTransLink vsss : vtls)
							{
								System.out.println("the type of vsss is:"+vsss.vtl_priority+"\tthis can offer bw:"+vsss.getAcutallyRestBWforShare()+"this not limited is :"+vsss.getAcutallyRestBWforShareNoLimit());//getAcutallyRestBW();
							}
							
							
						}
					}					
				}
				return true;
			}
		}
		System.out.println("check the input for mapping services");
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

	@Override
	public int getAvailableServiceNumber(int _source, int _dest, int _type) {
		if(_type == Service.TYPE_OTN)
		{
			return osm.getAvailableOTNsNumber(_source, _dest);
		}
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Service> getOnesToFitRequest(Service ss, int command, Map<Integer, Constraint> cons) {
		// TODO Auto-generated method stub
		//if(ss instanceof BandwidthTolerantPacketService)
		if(ss instanceof PacketService)
		{
			PacketService ps = (PacketService)ss;
			if(((PacketService) ss).priority != Service.PRIORITY_LOW)
				return null;

			int tem_bw = cons.get(Constraint.INITBW_C).value;
			cons.get(Constraint.INITBW_C).value = (int)(Service.MAX_ALLOWED_BW * VirtualTransLink.TH_USP_LOW);
			
			List<VirtualTransLink> target_vtls = vtlm.findFitVTLs(cons);
			VirtualTransLink tem_vtl;
			if(target_vtls != null && target_vtls.size()>0)
				return (List)target_vtls;
			
			cons.get(Constraint.INITBW_C).value = tem_bw;
			target_vtls = new ArrayList<VirtualTransLink>();
			tem_vtl = vtlm.findAnExtendedableVTL(cons);
			
			if(tem_vtl != null)
			{
				if(vtlrh.handlerRequest(tem_vtl, VirtualTransLink.EXTEND_REQUEST, null))//EXTEND 10G
				{
					target_vtls.add(tem_vtl);
					return (List)target_vtls;
				}
			}
			else
			{
				tem_vtl = (VirtualTransLink)establishNewOneToFitRequest(ps, Service.UseOTNService, cons);//new vtl 1G
				if(tem_vtl == null)
				{
					;//TODO adjust;
					return null;//TODO
				}
			}
	
			int allRequestBW = cons.get(Constraint.INITBW_C).value;			
			int canOfferBW = 0;
			int tem = 0;
			
			List<VirtualTransLink> _vtll = vtlm.getVTLs(tem_vtl.sourceVertex, tem_vtl.destVertex);
			for(VirtualTransLink vtl : _vtll)
			{
				if(vtl.type == VirtualTransLink.VTL_BOD)
				{
					tem = vtl.getAcutallyRestBWforShare();//getAcutallyRestBW();
					if(canOfferBW > 0)
					{
						canOfferBW += tem;
						target_vtls.add(vtl);
					}
					else if(canOfferBW<0)
						System.out.println("There must be some big mistake!! the canOfferBW is:"+canOfferBW);
				}
			}
			
			if(canOfferBW >= (Service.MAX_ALLOWED_BW * VirtualTransLink.TH_USP_LOW))
			{
				//TODO later. sort the vtls.
				System.out.println("very little channce to be here");
				return (List)target_vtls;
			}
			else
			{
				int _rest_need_bw_max = (int)(Service.MAX_ALLOWED_BW * VirtualTransLink.TH_USP_LOW) - canOfferBW;		
				int availble_otn_number = 0;
				
				availble_otn_number = osm.getAvailableOTNsNumber(tem_vtl.sourceVertex, tem_vtl.destVertex);//System.out.println("otn_1G_number"+otn_1G_number);
				//TODO if availble_otn_number == 0
				int bw_can_be_requested = 0;
				if((int)(availble_otn_number * Service.BW_1G * VirtualTransLink.TH_USP_LOW) >= _rest_need_bw_max)
					bw_can_be_requested = _rest_need_bw_max;
				else
				{
					bw_can_be_requested = (int)(availble_otn_number * Service.BW_1G * VirtualTransLink.TH_USP_LOW);
					if((int)(availble_otn_number * Service.BW_1G * VirtualTransLink.TH_USP_LOW) + canOfferBW < allRequestBW )
					{
						int differ_value = allRequestBW - canOfferBW - (int)(availble_otn_number * Service.BW_1G * VirtualTransLink.TH_USP_LOW);
						//TODO
						//Adjustment with chazhi
						//if adjustment failed, return null;
					}
				}
				
				Map<Integer,Constraint> _cons = new HashMap<Integer,Constraint>();
				Constraint _con = new Constraint(Constraint.INITBW_C,bw_can_be_requested,"Cons bw is"+bw_can_be_requested);
				_cons.put(Constraint.INITBW_C, _con);
				
				if(handleServiceRequest(tem_vtl, VirtualTransLink.EXTEND_REQUEST,_cons))
				{
					return (List)target_vtls;
				}
				else
				{
					System.out.println("This should not be happened!");
					return null;
				}
			}			
		}		
		return null;
	}

	@Override
	public Service getOneToFitRequest(Service ss, int command, Map<Integer, Constraint> cons) 
	{
		if(ss instanceof PacketService)
		{
			PacketService ps = (PacketService)ss;
			if(ps.priority == Service.PRIORITY_LOW)
				return null;
			
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


	
}
