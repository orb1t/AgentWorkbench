package gasmas.agents.components;

import gasmas.resourceallocation.InitialBehaviourMessageContainer;
import gasmas.resourceallocation.StatusData;
import jade.core.AID;
import agentgui.envModel.graph.networkModel.ClusterNetworkComponent;

public class ClusterNetworkComponentAgent extends GenericNetworkAgent {

	private static final long serialVersionUID = 5755894155609484866L;

	@Override
	protected void setup() {
		super.setup();

		// Inform every network component, that the component is part of the cluster
		for (String networkComponentID : ((ClusterNetworkComponent) myNetworkComponent).getNetworkComponentIDs()) {
			int tries = 0;
			while (this.sendAgentNotification(new AID(networkComponentID, AID.ISLOCALNAME), new InitialBehaviourMessageContainer(new StatusData(this.myNetworkComponent.getId()))) == false) {
				tries++;
				if (tries > 10) {
					System.out.println("PROBLEM (CCCA) to send a message to " + networkComponentID + " from " + this.getLocalName());
					break;
				}
			}
		}
	}

}