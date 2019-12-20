package util;

import fx.model.Person;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * Adapter (for JAXB) to convert between the Date and the ISO 8601
 * String representation of the date such as '2012-12-03'.
 */
public class XmlDateAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(String v) throws Exception {
        return Person.DATE_FORMATTER.parse(v);
    }

    @Override
    public String marshal(Date v) {
        return v.toString();
    }
}
