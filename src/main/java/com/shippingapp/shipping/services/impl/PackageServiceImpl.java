package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.shippingapp.shipping.component.Request;
import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.PackageTypeIsNullOrEmptyException;
import com.shippingapp.shipping.models.PackageSize;
import com.shippingapp.shipping.models.PackageType;
import com.shippingapp.shipping.services.PackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class PackageServiceImpl implements PackageService {

    private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);
    private static final Gson gson = new Gson();

    private static final String MESSAGE_TYPE = "{\"type\":\"packageType\"}";
    private static final String MESSAGE_SIZE = "{\"type\":\"packageSizeByType\",\"packageType\":\"%s\"}";
    private static final Type PACKAGE_TYPE_REFERENCE = new TypeReference<List<PackageType>>() {
    }.getType();
    private static final Type PACKAGE_SIZE_REFERENCE = new TypeReference<List<PackageSize>>() {
    }.getType();

    private final Request request;

    public PackageServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties) {
        request = new Request(connectionProperties, rabbitTemplate);
    }

    public List<String> getDescriptionsForPackageTypes() {
        String messageResponse = request.sendRequestAndReceiveResponse(MESSAGE_TYPE);
        logger.info("response package type {}", messageResponse);
        List<PackageType> packageTypes = gson.fromJson(messageResponse, PACKAGE_TYPE_REFERENCE);
        return getDescriptionTypesList(packageTypes);
    }

    private List<String> getDescriptionTypesList(List<PackageType> packageTypesList) {
        Set<PackageType> filteredPackageTypes = new HashSet<>(packageTypesList);
        return filteredPackageTypes
                .stream()
                .filter(pt -> pt.getId() != 0 && !pt.getDescription().isEmpty())
                .map(PackageType::getDescription)
                .collect(Collectors.toList());
    }

    public List<String> getDescriptionsForPackageSize(String packageType) {
        if (Objects.isNull(packageType) || packageType.trim().isEmpty()) {
            logger.error("packageType can't be empty or null");
            throw new PackageTypeIsNullOrEmptyException("Error to get package sizes");
        }
        String message = String.format(MESSAGE_SIZE, packageType);
        String messageResponse = request.sendRequestAndReceiveResponse(message);

        logger.info("response package size {}", messageResponse);
        List<PackageSize> packageSizes = gson.fromJson(messageResponse, PACKAGE_SIZE_REFERENCE);
        return getDescriptionSizesList(packageSizes);
    }

    private List<String> getDescriptionSizesList(List<PackageSize> packageSizes) {
        Set<PackageSize> filteredPackageSizes = new HashSet<>(packageSizes);
        return filteredPackageSizes
                .stream()
                .filter(ps -> ps.getId() != 0 && !ps.getDescription().isEmpty())
                .map(PackageSize::getDescription)
                .collect(Collectors.toList());
    }
}
