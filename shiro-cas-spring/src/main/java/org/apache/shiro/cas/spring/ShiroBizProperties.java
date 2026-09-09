package org.apache.shiro.cas.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>Shiro biz configuration properties.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@ConfigurationProperties(ShiroBizProperties.PREFIX)
@Getter
@Setter
@ToString
public class ShiroBizProperties {

	public static final String PREFIX = "shiro";

	/** The login page url. */
	private String loginUrl;
	/** The success url after login. */
	private String successUrl;
	/** The url to redirect to when authentication fails. */
	private String failureUrl;
	/** The url to redirect to when the request is unauthorized. */
	private String unauthorizedUrl;
	/** Whether caching is enabled. */
	private boolean cachingEnabled;
	/** Whether authentication caching is enabled. */
	private boolean authenticationCachingEnabled;
	/** The authentication cache name. */
	private String authenticationCacheName;
	/** Whether authorization caching is enabled. */
	private boolean authorizationCachingEnabled;
	/** The authorization cache name. */
	private String authorizationCacheName;
	/** Whether static security manager is enabled. */
	private boolean staticSecurityManagerEnabled;

}
