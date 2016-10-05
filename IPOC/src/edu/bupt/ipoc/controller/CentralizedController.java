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
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.ServiceGenerator;
import edu.bupt.ipoc.service.VirtualTransLink;

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
	}
	
	@Override
	public boolean handleServiceRequest(Service ss, int command, Map<Integer,Constraint> cons) {
		// TODO Auto-generated method stub
		
		if(ss instanceof PacketService)
		{
			//Packset Service Request Handler
			if(psrh.handlerRequest(ss, command, cons))
			{
				if(command == PacketService.CARRIED_REQUEST)
					this.saveService(ss);
				return true;
			}			
		}		
		else if(ss instanceof OpticalService)
		{
			
		}
		else if(ss instanceof VirtualTransLink)
		{
			if(vtlrh.handlerRequest(ss, command, cons))
			{
				if(command == VirtualTransLink.BUILD_REQUEST)
					this.saveService(ss);
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
			PacketService ps = (PacketService) ss;
			if(command == PacketService.CARRIED_REQUEST)
			{
				cons.put(Constraint.SOURCE_C, new Constraint(Constraint.SOURCE_C,ps.sourceVertex,"ps's source id:"+ps.sourceVertex));
				cons.put(Constraint.DEST_C, new Constraint(Constraint.DEST_C,ps.sinkVertex,"ps's DEST id:"+ps.sinkVertex));
				cons.put(Constraint.PRIORITY_C, new Constraint(Constraint.PRIORITY_C,ps.s_priority,"ps's priority:"+ps.s_priority));
				if(cons.get(Constraint.PS_CARRIED_TYPE)!=null && cons.get(Constraint.PS_CARRIED_TYPE).value == PacketService.STATIC_CARRIED)
				{
					cons.put(Constraint.INITBW_C, new Constraint(Constraint.INITBW_C,(int)(ps.getStaticBw()*1.3),"ps's request static bw:"+(int)(ps.getStaticBw()*1.3)));
					System.out.println(cons.get(Constraint.INITBW_C));
				}
				else
					cons.put(Constraint.INITBW_C, new Constraint(Constraint.INITBW_C,ps.getCurrentBw(),"ps's request bw:"+ps.getCurrentBw()));
								
				//there should be a cons here	
				return vtlm.findFitVTL(cons);
			}
		}
		else if(ss instanceof OTNService)
		{
			OTNService otn = (OTNService) ss;
			return osm.findFitOs(otn.sourceVertex, otn.destVertex, OpticalService.CHANNEL_10G_FOT_OTN, OTNService.OTN_1G);
		}
		return null;
	}
	
	@Override
	public List<Service> findExistOnesToFitRequest(Service ss, int command, Map<Integer,Constraint> cons) {
		return null;
	}

	@Override
	public Service establishNewOneToFitRequest(Service ss, int command, Map<Integer,Constraint> cons) {
		// TODO Auto-generated method stub
		
		if(ss instanceof PacketService)
		{
			PacketService ps = (PacketService) ss;
			
			VirtualTransLink _vtl = new VirtualTransLink();
			_vtl.setID(ServiceGenerator.generate_an_id());
			_vtl.setSourceAndDest(ps.sourceVertex, ps.sinkVertex);
			_vtl.setPrioriy(ps.s_priority);
			
			//the cons already be added when they try to find existing vtl			
			//cons.put(Constraint.INITBW_C, new Constraint(Constraint.INITBW_C,ps.getCurrentBw(),"ps current need bw is"+ps.getCurrentBw()));
			
			if(vtlrh.handlerRequest(_vtl, VirtualTransLink.BUILD_REQUEST, cons))
			{
				this.saveService(_vtl);
				return _vtl;
			}					
			//return 		
		}
		else if(ss instanceof VirtualTransLink)
		{
			VirtualTransLink _vtl = (VirtualTransLink) ss;
			
			if((_vtl.vtl_priority == VirtualTransLink.VTL_P_HIGH || _vtl.vtl_priority == VirtualTransLink.VTL_P_MID) && command != VTLRequestHandler.UseOpticalService )
			{
				OTNService _otn = new OTNService(ServiceGenerator.generate_an_id(),_vtl.sourceVertex, _vtl.destVertex);
				
				if(otnrh.handlerRequest(_otn, OTNService.BUILD_REQUEST, null))
				{
					this.saveService(_otn);
					return _otn;
				}
			}
			else if(_vtl.vtl_priority == VirtualTransLink.VTL_P_LOW || command == VTLRequestHandler.UseOpticalService )
			{
				OpticalService _os = new OpticalService(ServiceGenerator.generate_an_id(),
						_vtl.sourceVertex, _vtl.destVertex, 1, 0);
				
				if(orrh.handlerRequest(_os, OpticalService.BUILD_REQUEST, null))
				{
					_os.setType(OpticalService.CHANNEL_10G_SINGLE);
					this.saveService(_os);
					return _os;
				}				
			}		
		}
		else if(ss instanceof OTNService)
		{
			OTNService _otns = (OTNService) ss;
			
			OpticalService _os = new OpticalService(ServiceGenerator.generate_an_id(),
					_otns.sourceVertex, _otns.destVertex, 1, 0);
			
			if(orrh.handlerRequest(_os, OpticalService.BUILD_REQUEST, null))
			{
				_os.setType(OpticalService.CHANNEL_10G_FOT_OTN);
				this.saveService(_os);
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
			
			if(command == VirtualTransLink.BUILD_REQUEST)
			{
				if(_init_bw_c < OTNService.BW_1G)
				{
					Service _ss = establishNewOneToFitRequest(_vtl,0,cons);
					if(_ss != null)
					{
						List<Service> ssl = new ArrayList<Service>();
						ssl.add(_ss);
						return ssl;
					}				
				}
				else
				{
					OpticalService _os = osm.findFitOs(_vtl.sourceVertex,_vtl.destVertex,OpticalService.CHANNEL_10G_FOT_OTN,_init_bw_c);
					if(_os != null)
						return setUpOTNServices(_os,_init_bw_c);
				}
			}			
			else if(command == VirtualTransLink.EXTEND_REQUEST)
			{
				//List<Service> ssl = new ArrayList<Service>();
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
		return false;
	}

	@Override
	public void saveService(Service ss) {
		
		if(ss instanceof PacketService)
		{
			psm.addService(ss);
		}
		else if(ss instanceof OpticalService)
		{
			osm.addService(ss);
		}
		else if(ss instanceof VirtualTransLink)
		{
			vtlm.addService(ss);
		}
		else if(ss instanceof OTNService)
		{
			otnsm.addService(ss);
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
			if(requestBW < 0)
				break;		
		}
		return ssl;
	}

	
}
