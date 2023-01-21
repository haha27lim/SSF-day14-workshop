package sg.edu.nus.iss.app.day14workshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.app.day14workshop.models.Contact;

@Qualifier("contactsRedis")
@Service
public class ContactsRedis {

    // Defining the constant for Contact Entity
    private static final String CONTACT_ENTITY = "contactlist";
    
    //Autowiring the RedisTemplate
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    // Method to save Contact
    public void save(final Contact ctc) {
        // Inserting the contact Id in the List
        redisTemplate.opsForList().leftPush(CONTACT_ENTITY, ctc.getId());
        // Inserting the contact in the Map
        redisTemplate.opsForHash().put(CONTACT_ENTITY + "_Map", ctc.getId(), ctc);
    }

    // Method to find Contact by Id
    public Contact findById(final String contactId) {
        // Fetching the contact from map
        Contact result = (Contact)redisTemplate.opsForHash().get(CONTACT_ENTITY + "_Map", contactId);
        // Returning the contact
        return result;
    }

    // Method to find all the contacts
    public List<Contact> findAll(int startIndex) {
        // Fetching the ids from list
        List<Object> fromContactList = redisTemplate.opsForList().range(CONTACT_ENTITY, startIndex, 10);

        // Fetching the contacts from map
        List<Contact> ctcs = redisTemplate.opsForHash()
                                .multiGet(CONTACT_ENTITY + "_Map", fromContactList)
                                .stream()
                                .filter(Contact.class::isInstance)
                                .map(Contact.class::cast)
                                .toList();

        // Returning the contacts
        return ctcs;
    }


}
