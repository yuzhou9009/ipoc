package edu.bupt.ipoc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.handler.OTNRequestHandler;
import edu.bupt.ipoc.handler.OpticalResourceRequestHandler;
import edu.bupt.ipoc.handler.PacketServiceRequestHandler;
import edu.bupt.ipoc.handler.VTLRequestHandler;
import edu.bupt.ipoc.managers.OTNServiceManager;
import edu.bupt.ipoc.managers.OpticalServiceManager;
import edu.bupt.ipoc.managers.PacketServiceManager;
import edu.bupt.ipoc.managers.VTLManager;
import edu.bupt.ipoc.service.OTNService;
import edu.bupt.ipoc.service.OpticalService;
import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.PacketServiceChild;
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
		
		psrh = new PacketServiceRequestHandler(this);
		vtlrh = new VTLRequestHandler(this);
		orrh = new OpticalResourceRequestHandler(_graph_G, this);
		otnrh = new OTNRequestHandler(this);
		sst = new SimpleStatisticTool(this);
	}
	
	@Override
	public boolean handleServiceRequest(Service ss, int command, Map<Integer,Constraint> cons) {
		// TODO Auto-generated method stub
		if(ss instanceof PacketService)
		{	
			PacketService _ps = (PacketService)ss;
			if(psrh.handlerRequest(_ps, command, cons))
			{
				if(command == PacketService.CARRIED_REQUEST)
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
				if(command == OTNService.BUILD_REQUEST)
					otnsm.addService(_otns);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Service findExistOneToFitRequest(Service ss, int command, Map<Integer,Constraint> cons) {
		// TODO Auto-generated method stub
		if(ss instanceof PacketService)
		{
			if(command == PacketService.CARRIED_REQUEST)
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
			if(command == PacketService.CARRIED_REQUEST)
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
			
			if((_vtl.vtl_priority == VirtualTransLink.PRIORITY_HIGH || _vtl.vtl_priority == VirtualTransLink.PRIORITY_MID) && command != VTLRequestHandler.UseOpticalService )
			{
				OTNService _otn = new OTNService(ServiceGenerator.generate_an_id(),_vtl.sourceVertex, _vtl.destVertex);
				
				if(this.handleServiceRequest(_otn, OTNService.BUILD_REQUEST, null))
				{
					return _otn;
				}
			}
			else if(_vtl.vtl_priority == VirtualTransLink.PRIORITY_LOW || command == VTLRequestHandler.UseOpticalService )
			{
				OpticalService _os = new OpticalService(ServiceGenerator.generate_an_id(),
						_vtl.sourceVertex, _vtl.destVertex, 1, 0);
				if(this.handleServiceRequest(_os, OpticalService.BUILD_REQUEST, null))
				{
					_os.setType(OpticalService.CHANNEL_10G_SINGLE);
					return _os;
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
				System.out.println("There must be the INITBW_C constraint!");
				return null;
			}
			
			VirtualTransLink _vtl = (VirtualTransLink) ss;
			
			if(_vtl.type == VirtualTransLink.CAN_NOT_BE_EXTENDED_BUT_SHARED || _vtl.type == VirtualTransLink.CAN_NOT_BE_EXTENDED_OR_SHARED)
			{
				_init_bw_c = (int)(_init_bw_c * Service.SURVIVABILITY_FACTOR)+1;
			}
			else if(_vtl.type == VirtualTransLink.VTL_BOD)//This request with low priority will not be here
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
					//need to be extended, we should use all otns which have the same path, not limited the same os.
					OpticalService _os = osm.findFitOs(_vtl.sourceVertex,_vtl.destVertex,OpticalService.CHANNEL_10G_FOT_OTN,_init_bw_c);
					if(_os != null)
					{
						return setUpOTNServices(_os,_init_bw_c);
					}
					else
					{
						_os = new OpticalService(ServiceGenerator.generate_an_id(),
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
					;//later
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
										
					int _all_bw = ps.getCurrentOccupiedBw();
					int _bw = 0;
					ps.sub_packet_services = new ArrayList<PacketServiceChild>();
					for(VirtualTransLink vtl : vtls)
					{
						_bw = vtl.getAcutallyRestBWforShare();//getAcutallyRestBW();
						if(_bw > _all_bw)
						{
							_bw = _all_bw;
						}
						PacketServiceChild psc = null;
						if(_bw <= PacketService.STATIC_MIN_RATE)
						{
							psc = new PacketServiceChild(ServiceGenerator.generate_an_id(),
									ps.sourceNode,ps.destNode,ps.priority,PacketService.STATIC_MIN_RATE,0);

							//_bw = PacketService.STATIC_MIN_RATE;
						}
						else
						{
							psc = new PacketServiceChild(ServiceGenerator.generate_an_id(),
									ps.sourceNode,ps.destNode,ps.priority,_bw,0);
						}
						
						psc.setPsFather(ps);
						_all_bw -= _bw;
						ps.sub_packet_services.add(psc);
						mappingServices(psc, vtl, null);							
					}
					if(_all_bw>0)
						System.out.println("There is a big mistake come on the _all_bw is"+ _all_bw);
					
				}
				return true;
			}
		}
		System.out.println("check the input for mapping services");
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

	@Override
	public void clearAll() {
		graph_G.clear_all_resource();
		
		psm.clearAllServices();
		osm.clearAllServices();
		vtlm.clearAllServices();
		otnsm.clearAllServices();		
	}
	
}
