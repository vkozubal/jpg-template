package nl.lincsafe.bsc.configuration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Handle marshaling/umarshaling of its subclasses
 */
@XmlSeeAlso({Config.class})
public class AbstractConfig {

    public AbstractConfig() {
    }

    private static Marshaller createMarshaller() throws JAXBException {
        Marshaller jaxbMarshaller = ContextHolder.JAXB_CONTEXT.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return jaxbMarshaller;
    }

    public String marshalToString() throws JAXBException {
        StringWriter writer = new StringWriter();
        createMarshaller().marshal(this, writer);
        return writer.toString();
    }

    public static <T> T unmarshalFromString(String data) throws JAXBException {
        StringReader reader = new StringReader(data);
        try {
            return (T) createUnmarshaller().unmarshal(reader);
        } finally {
            reader.close();
        }
    }

    private static Unmarshaller createUnmarshaller() throws JAXBException {
        return ContextHolder.JAXB_CONTEXT.createUnmarshaller();
    }

    // JAXBContext holder
    @XmlTransient
    private static class ContextHolder {
        public static final JAXBContext JAXB_CONTEXT;

        static {
            JAXBContext temporary;
            try {
                temporary = JAXBContext.newInstance(AbstractConfig.class);
            } catch (JAXBException e) {
                throw new IllegalStateException("JAXB context could not be initialized", e);
            }
            JAXB_CONTEXT = temporary;
        }
    }
}
