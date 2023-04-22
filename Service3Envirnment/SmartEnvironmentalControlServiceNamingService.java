package Service3Envirnment;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class SmartEnvironmentalControlServiceNamingService {
    private static final Logger logger = Logger.getLogger(SmartEnvironmentalControlServiceNamingService.class.getName());

    private static final String SERVICE_TYPE = "_smart_environmental_control._tcp.local.";

    private JmDNS jmdns;

    public void register(int port) {
        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());

            ServiceInfo serviceInfo = ServiceInfo.create(SERVICE_TYPE, "Smart Environmental Control Service", port,
                    "Service for monitoring and controlling air and water quality in indoor environments.");
            jmdns.registerService(serviceInfo);

            logger.info("Registered Smart Environmental Control Service with jmDNS");
        } catch (UnknownHostException e) {
            logger.severe("Could not register Smart Environmental Control Service with jmDNS due to unknown host");
        } catch (IOException e) {
            logger.severe("Could not register Smart Environmental Control Service with jmDNS due to IO exception: " + e.getMessage());
        }
    }

    public void discover() {
        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            ServiceInfo[] serviceInfos = jmdns.list(SERVICE_TYPE);

            if (serviceInfos.length == 0) {
                logger.warning("No Smart Environmental Control Service instances found");
            } else {
                logger.info("Found " + serviceInfos.length + " Smart Environmental Control Service instance(s)");
                for (ServiceInfo serviceInfo : serviceInfos) {
                    logger.info("Service Info: " + serviceInfo);
                }
            }
        } catch (UnknownHostException e) {
            logger.severe("Could not discover Smart Environmental Control Service instances with jmDNS due to unknown host");
        } catch (IOException e) {
            logger.severe("Could not discover Smart Environmental Control Service instances with jmDNS due to IO exception: " + e.getMessage());
        }
    }

    public void unregister() {
        if (jmdns != null) {
            jmdns.unregisterAllServices();
            try {
                jmdns.close();
            } catch (IOException e) {
                logger.severe("Could not close jmDNS instance: " + e.getMessage());
            }
            logger.info("Unregistered Smart Environmental Control Service from jmDNS");
        }
    }
}
