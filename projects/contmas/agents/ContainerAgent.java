/**
 * 
 */
package contmas.agents;

import contmas.ontology.*;
import jade.content.AgentAction;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.leap.LEAPCodec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

/**
 * @author Hanno - Felix Wagner
 *
 */
public class ContainerAgent extends Agent {
	protected Codec codec = new LEAPCodec();
	protected Ontology ontology = ContainerTerminalOntology.getInstance();
	protected String serviceType;
	protected ContainerHolder ontologyRepresentation;
	
	protected List contractors=null;

	
	public Integer lengthOfQueue=2;
	protected List loadOrdersProposedForQueue=new ArrayList();
	
	protected List announcedQueue=new ArrayList();
	
	protected List failedQueue=new ArrayList();
	
	protected List pendingQueue=new ArrayList();

	
	public ContainerAgent() {
		this.serviceType="handling-containers";
		this.ontologyRepresentation=new ContainerHolder();
	}
	
	public ContainerAgent(String serviceType) {
		this();
		this.serviceType=serviceType;
	}
	
	public ContainerAgent(String serviceType, ContainerHolder ontologyRepresentation) {
		this(serviceType);
		this.ontologyRepresentation=ontologyRepresentation;
	}
	protected void setup(){ 
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		//register self at DF
        ServiceDescription sd  = new ServiceDescription();
        sd.setType( serviceType );
        sd.setName( getLocalName() );
        register( sd );
        
        if(ontologyRepresentation.getContains()==null){
    		BayMap LoadBay=new BayMap();
    		LoadBay.setX_dimension(1);
    		LoadBay.setY_dimension(1);
    		LoadBay.setZ_dimension(1);
    		ontologyRepresentation.setContains(LoadBay);
        }
	}
	public Integer getBaySize(){
		BayMap LoadBay=ontologyRepresentation.getContains();
		Integer baySize=LoadBay.getX_dimension()*LoadBay.getY_dimension()*LoadBay.getZ_dimension();
		return baySize;
	}
	public Integer getBayUtilization(){
		return ontologyRepresentation.getContains().getIs_filled_with().size();
	}
    void register( ServiceDescription sd)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {  
            DFService.register(this, dfd );  
        }
        catch (FIPAException fe) { fe.printStackTrace(); }
    }
    public DFAgentDescription[] getAgentsFromDF(String serviceType){
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd  = new ServiceDescription();
        sd.setType( serviceType );
        dfd.addServices(sd);
        try {
			DFAgentDescription[] result = DFService.search(this, dfd);
			return result;
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    public AID[] getAIDsFromDF(String serviceType){
    	DFAgentDescription[] agents=getAgentsFromDF(serviceType);
		AID[] result= new AID[agents.length];
		for (int i = 0; i < agents.length; i++) {
			result[i]=agents[i].getName();
		}
		return result;
    }
    public AID getFirstAIDFromDF(String serviceType){
    	AID[] aids=getAIDsFromDF(serviceType);
    	if(aids.length>0){
    		return aids[0];
    	}else{
    		System.err.println("Kein Agent der Art vorhanden.");
    	}
    	return null;
    }
    public List toAIDList(AID[] input){
    	List output=new ArrayList();
		for (AID aid : input) {
			output.add(aid);
		}
    	return output;
    }
    
    public TransportOrder findMatchingOrder(TransportOrderChain haystack){
    	return findMatchingOrder(haystack,true);
    }
    
    public TransportOrder findMatchingOrder(TransportOrderChain haystack, boolean matchIncoming){
    	Iterator toc=haystack.getAllIs_linked_by();
    	TransportOrder matchingOrder=null;
		Integer matchRating=-1;
		Integer bestMatchRating=-1;
		//echoStatus("findMatchingOrder - jede order in der kette durchlaufen");
    	while(toc.hasNext()){
//			echoStatus("Ausschreibung ausprobieren.");

    		TransportOrder curTO=(TransportOrder) toc.next();
    		if(!matchIncoming && matchAID(curTO.getStarts_at())){
    			return curTO;
    		}
    		matchRating=matchOrder(curTO);
    		if(matchRating>-1 && matchIncoming){
    			if(bestMatchRating==-1 || bestMatchRating>matchRating){
	    			matchingOrder=curTO;
	    			bestMatchRating=matchRating;
//	    			echoStatus("Ausschreibung passt zu mir! (besser?)");
    			}
    		}
    	}
    	return matchingOrder;
    }
    
    
    /*
     * Matcht hier nur Habitat des Ziels (f�r Static und Passive).
     * Matching f�r Active ist angepasst, matcht sowohl Start als auch Ziel
     * und Habitat+Capabilities
     * Matching-Bewertung:
     * -1 NoMatch
     *  0 ExactMatch (AID), Static+Passive
     *  1 ExactMatch (AID), Active
     *  2 relativeMatch (Domain difference), Minimum f�r Static+Passive
     * ...relativeMatch 
     * 
     * Matching-Wert stellt also nahezu Aufwand des Transports dar
     */
	public Integer matchOrder(TransportOrder curTO){ 
		Designator end=(Designator) curTO.getEnds_at();
		if(matchAID(end)){ //Genau f�r mich bestimmt
			return 0;
		} else {
			Domain endHabitat=(Domain) end.getAbstract_designation();
			Domain ownHabitat=ontologyRepresentation.getLives_in();
			if(endHabitat.getClass()==ownHabitat.getClass()){ //domain entspricht genau Lebensraum
				return 2; //TODO +DomainDiffrence
			}
		}
		return -1; //order passt gar nicht
	}
	
	public Boolean matchAID(Designator designation){
		if(designation.getType().equals("concrete")){
			if(designation.getConcrete_designation().equals(this.getAID())){ //genau f�r diesen Agenten bestimmt
				return true;
			}
		} 
		return false;
	}
    
    public List determineContractors(){
    	ArrayList contractors=new ArrayList();
    	return contractors;
    }
    public void echoStatus(String statusMessage){
    	System.out.println(this.getAID().getLocalName()+": "+statusMessage);
    }
    
    public void echoStatus(String statusMessage, TransportOrderChain subject){
    	String additionalText="";
    	if(subject!=null){
    		additionalText=" BIC="+subject.getTransports().getId();
    	}
    	echoStatus(statusMessage+additionalText);
    }
    
	public BlockAddress getEmptyBlockAddress(){
		BlockAddress empty=new BlockAddress();
		empty.setX_dimension(0);
		empty.setY_dimension(0);
		empty.setZ_dimension(0);
		return empty;
	}
	public ProposeLoadOffer GetLoadProposal(TransportOrderChain curTOC){
    	ProposeLoadOffer act=null;
    	TransportOrder matchingOrder=findMatchingOrder(curTOC);
    	if(matchingOrder!=null){ //passende TransportOrder gefunden
//			echoStatus("TransportOrder gefunden, die zu mir passt.",curTOC);
			matchingOrder.getEnds_at().setConcrete_designation(getAID());
			matchingOrder.getEnds_at().setType("concrete");

    		act=new ProposeLoadOffer();
    		calculateEffort(matchingOrder);
    		act.setLoad_offer(curTOC);
    		loadOrdersProposedForQueue.add(curTOC);
    	} else {
			echoStatus("keine TransportOrder passt zu mir.",curTOC);
    	}
		return act;
	}
	
	public TransportOrder calculateEffort(TransportOrder call){
		call.setTakes(0);
		return call;		
	}
	
	public Boolean aquireContainer(TransportOrderChain targetContainer){ //eigentlicher Vorgang des Container-Aufnehmens
		((ContainerHolder)this.ontologyRepresentation).getAdministers().addConsists_of(targetContainer); //Container zu Auftragsbuch hinzuf�gen
		
		//physikalische Aktionen
		
		BlockAddress destination=getEmptyBlockAddress(); //zieladresse besorgen
		destination.setLocates(targetContainer.getTransports());
		ontologyRepresentation.getContains().addIs_filled_with(destination); //Container mit neuer BlockAdress in eigene BayMap aufnehmens
//		echoStatus("Nun wird der Container von mir transportiert");
		if(removeFromQueue(targetContainer)){ //Auftrag aus Liste von Bewerbungen streichen
			return true;
		}
		echoStatus("ERROR: Ausschreibung, auf die ich mich beworben habe, nicht gefunden.",targetContainer);
		return false;
	}
	

	
	public boolean removeFromQueue(TransportOrderChain proposedTOC){
		Iterator queue=loadOrdersProposedForQueue.iterator();

		while(queue.hasNext()){ //Auftr�ge durchlaufen, auf die beworben wurde, den richtigen entfernen
//			echoStatus("Queue �berpr�fen, Auftr�ge durchlaufen, auf die beworben wurde, den richtigen entfernen");
			TransportOrderChain queuedTOC=(TransportOrderChain)queue.next();
			if(proposedTOC.getTransports().getId().equals(queuedTOC.getTransports().getId())){ //wenn der untersuchte Container dem entspricht, f�r den sich beworben wurde
				queue.remove();
//				echoStatus("Auftrag aus Bewerbungsliste entfernt");
				return true;
			}
		}
		return false;
	}
	
	public boolean removeFromAnnouncedQueue(TransportOrderChain needleTOC){
		Iterator queue=announcedQueue.iterator();
		while(queue.hasNext()){
			TransportOrderChain queuedTOC=(TransportOrderChain)queue.next();
			if(needleTOC.getTransports().getId().equals(queuedTOC.getTransports().getId())){
				queue.remove();
				return true;
			}
		}
		return false;
	}

	public boolean removeFromCommissions(TransportOrderChain needleTOC){
		Iterator queue=ontologyRepresentation.getAdministers().getAllConsists_of();
		while(queue.hasNext()){
			TransportOrderChain queuedTOC=(TransportOrderChain)queue.next();
			if(needleTOC.getTransports().getId().equals(queuedTOC.getTransports().getId())){
				queue.remove();
				return true;
			}
		}
		return false;
	}
	
	public boolean isInAnnouncedQueue(TransportOrderChain needleTOC){
		Iterator queue=announcedQueue.iterator();
		while(queue.hasNext()){
			TransportOrderChain queuedTOC=(TransportOrderChain)queue.next();
			if(needleTOC.getTransports().getId().equals(queuedTOC.getTransports().getId())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInFailedQueue(TransportOrderChain needleTOC){
		Iterator queue=failedQueue.iterator();
		while(queue.hasNext()){
			TransportOrderChain queuedTOC=(TransportOrderChain)queue.next();
			if(needleTOC.getTransports().getId().equals(queuedTOC.getTransports().getId())){
				return true;
			}
		}
		return false;
	}
	
	public boolean removeFromPendingQueue(TransportOrderChain needleTOC){
		Iterator queue=pendingQueue.iterator();
		while(queue.hasNext()){
			TransportOrderChain queuedTOC=(TransportOrderChain)queue.next();
			if(needleTOC.getTransports().getId().equals(queuedTOC.getTransports().getId())){
				queue.remove();
				return true;
			}
		}
		return false;
	}
	
	public boolean isAlreadyPending(TransportOrderChain needleTOC){
		Iterator queue=pendingQueue.iterator();
		while(queue.hasNext()){
			TransportOrderChain queuedTOC=(TransportOrderChain)queue.next();
			if(needleTOC.getTransports().getId().equals(queuedTOC.getTransports().getId())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isQueueNotFull(){ //TODO use DataStore
		//echoStatus("lengthOfQueue: "+lengthOfQueue+", loadOrderPostQueue.size(): "+loadOrdersProposedForQueue.size());
		return loadOrdersProposedForQueue.size()<lengthOfQueue;
	}
	
	public boolean hasBayMapRoom(){
		return getBaySize()>getBayUtilization();
	}
	
	public boolean checkPlausibility(CallForProposalsOnLoadStage call){
		return true;
	}

	public boolean removeContainerFromBayMap(TransportOrderChain load_offer) {
//		echoStatus("removeContainerFromBayMap:",load_offer);
		Iterator allContainers=ontologyRepresentation.getContains().getIs_filled_with().iterator();
		while(allContainers.hasNext()){
			Container curContainer=((BlockAddress)allContainers.next()).getLocates();
//			echoStatus("curContainerID: "+curContainer.getId()+"load_offerID: "+load_offer.getTransports().getId());
			if(curContainer.getId().equals(load_offer.getTransports().getId())){
				allContainers.remove();
//				echoStatus("Container found and removed.",load_offer);
				return true;
			}
		}
		echoStatus("ERROR: Container NOT found to remove from BayMap.",load_offer);
		return false;
	}
	
	public boolean removeFromContractors(AID badContractor){
		Iterator contractorList=contractors.iterator();
		while(contractorList.hasNext()){
			AID contractor=(AID)contractorList.next();
			if(badContractor.equals(contractor)){
				contractorList.remove();
				return true;
			}
		}
		return false;
	}

	public void fillMessage(ACLMessage msg, AgentAction act) {
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		try {
			getContentManager().fillContent(msg, act);
		}catch (UngroundedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CodecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public AgentAction extractAction(ACLMessage msg) {
		AgentAction act=null;
		try {
			act=(AgentAction) getContentManager().extractContent(msg);
		}catch (UngroundedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CodecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return act;
	}
}