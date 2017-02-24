package org.springframework.boot.context.embedded;

/**
 * @author bzumpano
 * @since 24/02/17
 *
 * Workarround para o assetPipeline funcionar, pois não está dando erro com o Spring boot 1.5.1
 * @see <a href="https://github.com/bertramdev/asset-pipeline/issues/145" />
 *
 */
public class FilterRegistrationBean extends org.springframework.boot.web.servlet.FilterRegistrationBean {
}
