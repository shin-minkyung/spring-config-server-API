/*
 * package com.config; import java.util.Collections; import java.util.HashMap;
 * import java.util.LinkedHashSet; import java.util.List; import java.util.Map;
 * import java.util.Set;
 * 
 * import org.apache.commons.logging.Log; import
 * org.apache.commons.logging.LogFactory;
 * 
 * import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
 * import org.springframework.cloud.config.monitor.PropertyPathNotification;
 * import
 * org.springframework.cloud.config.monitor.PropertyPathNotificationExtractor;
 * import org.springframework.context.ApplicationEventPublisher; import
 * org.springframework.context.ApplicationEventPublisherAware; import
 * org.springframework.http.HttpHeaders; import
 * org.springframework.http.MediaType; import
 * org.springframework.util.StringUtils; import
 * org.springframework.web.bind.annotation.RequestBody; import
 * org.springframework.web.bind.annotation.RequestHeader; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RequestMethod; import
 * org.springframework.web.bind.annotation.RequestParam; import
 * org.springframework.web.bind.annotation.RestController;
 * 
 * @RestController public class ConfigRestController implements
 * ApplicationEventPublisherAware{
 * 
 * private static Log log = LogFactory.getLog(ConfigRestController.class);
 * 
 * private PropertyPathNotificationExtractor extractor;
 * 
 * private ApplicationEventPublisher applicationEventPublisher;
 * 
 * private String busId;
 * 
 * public void PropertyPathEndpoint(PropertyPathNotificationExtractor extractor,
 * String busId) { this.extractor = extractor; this.busId = busId; }
 * 
 * for testing String getBusId() { return this.busId; }
 * 
 * 
 * 
 * @RequestMapping(method = RequestMethod.POST) public Set<String>
 * notifyByPath(@RequestHeader HttpHeaders headers,
 * 
 * @RequestBody Map<String, Object> request) { PropertyPathNotification
 * notification = this.extractor.extract(headers, request); if (notification !=
 * null) {
 * 
 * Set<String> services = new LinkedHashSet<>();
 * 
 * for (String path : notification.getPaths()) {
 * services.addAll(guessServiceName(path)); } if (this.applicationEventPublisher
 * != null) { for (String service : services) { log.info("Refresh for: " +
 * service); this.applicationEventPublisher.publishEvent( new
 * RefreshRemoteApplicationEvent(this, this.busId, service)); } return services;
 * }
 * 
 * } return Collections.emptySet(); }
 * 
 * @RequestMapping(method = RequestMethod.POST, consumes =
 * MediaType.APPLICATION_FORM_URLENCODED_VALUE) public Set<String>
 * notifyByForm(@RequestHeader HttpHeaders headers,
 * 
 * @RequestParam("path") List<String> request) { Map<String, Object> map = new
 * HashMap<>(); String key = "path"; map.put(key, request); return
 * notifyByPath(headers, map); }
 * 
 * private Set<String> guessServiceName(String path) { Set<String> services =
 * new LinkedHashSet<>(); if (path != null) { String stem =
 * StringUtils.stripFilenameExtension(
 * StringUtils.getFilename(StringUtils.cleanPath(path))); // TODO: correlate
 * with service registry int index = stem.indexOf("-"); while (index >= 0) {
 * String name = stem.substring(0, index); String profile = stem.substring(index
 * + 1); if ("application".equals(name)) { services.add("*:" + profile); } else
 * if (!name.startsWith("application")) { services.add(name + ":" + profile); }
 * index = stem.indexOf("-", index + 1); } String name = stem; if
 * ("application".equals(name)) { services.add("*"); } else if
 * (!name.startsWith("application")) { services.add(name); } } return services;
 * }
 * 
 * @Override public void setApplicationEventPublisher(ApplicationEventPublisher
 * applicationEventPublisher) { this.applicationEventPublisher =
 * applicationEventPublisher; }
 * 
 * 
 * }
 */