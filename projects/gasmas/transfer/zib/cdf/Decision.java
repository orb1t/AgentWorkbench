//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.01 at 12:34:49 AM MESZ 
//


package gasmas.transfer.zib.cdf;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://www.am.uni-erlangen.de/wima/CombinedDecisions/XMLSchema}valve"/>
 *         &lt;element ref="{http://www.am.uni-erlangen.de/wima/CombinedDecisions/XMLSchema}controlValve"/>
 *         &lt;element ref="{http://www.am.uni-erlangen.de/wima/CombinedDecisions/XMLSchema}compressorStation"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.am.uni-erlangen.de/wima/CombinedDecisions/XMLSchema}idGroup"/>
 *       &lt;attribute name="fullName" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "valveOrControlValveOrCompressorStation"
})
@XmlRootElement(name = "decision")
public class Decision {

    @XmlElements({
        @XmlElement(name = "compressorStation", type = CompressorStation.class),
        @XmlElement(name = "controlValve", type = ControlValve.class),
        @XmlElement(name = "valve", type = Valve.class)
    })
    protected List<Object> valveOrControlValveOrCompressorStation;
    @XmlAttribute
    protected String fullName;
    @XmlAttribute(required = true)
    protected String id;

    /**
     * Gets the value of the valveOrControlValveOrCompressorStation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valveOrControlValveOrCompressorStation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValveOrControlValveOrCompressorStation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompressorStation }
     * {@link ControlValve }
     * {@link Valve }
     * 
     * 
     */
    public List<Object> getValveOrControlValveOrCompressorStation() {
        if (valveOrControlValveOrCompressorStation == null) {
            valveOrControlValveOrCompressorStation = new ArrayList<Object>();
        }
        return this.valveOrControlValveOrCompressorStation;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        if (fullName == null) {
            return "";
        } else {
            return fullName;
        }
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}