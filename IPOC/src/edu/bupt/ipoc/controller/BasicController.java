package edu.bupt.ipoc.controller;

import edu.bupt.ipoc.service.PacketService;
import edu.bupt.ipoc.service.Service;
import edu.bupt.ipoc.service.VirtualTransLink;

import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.*;

public interface BasicController {
		
	public boolean handleServiceRequest(Service ss, int command, Map<Integer,Constraint> cons);
	public List<Service> getOnesToFitRequest(Service ss, int command, Map<Integer,Constraint> cons);
	public Service getOneToFitRequest(Service ss, int command, Map<Integer,Constraint> cons);
	public Service findExistOneToFitRequest(Service ss, int command, Map<Integer,Constraint> cons);
	public List<Service> findExistOnesToFitRequest(Service ss, int command, Map<Integer,Constraint> cons);
	public Service establishNewOneToFitRequest(Service ss, int command, Map<Integer,Constraint> cons);
	public List<Service> establishNewOnesToFitRequest(Service ss, int command, Map<Integer,Constraint> cons);
	public boolean mappingServices(Service ss1, Service ss2, Map<Integer,Constraint> cons);
	public boolean mappingServices(Service ss, List<Service> _tems, Map<Integer, Constraint> cons);
	public boolean unmappingServices(Service ss, List<Service> _tems, Map<Integer, Constraint> cons);
	public int getAvailableServiceNumber(int source, int dest, int type);
	
	public void saveService(Service ss);	
	public void clearAll();


}
