package com.airticket.booking.service;

import com.airticket.booking.dto.BookingDTO;
import com.airticket.booking.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    
    BookingDTO toDTO(Booking booking);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "bookingReference", ignore = true)
    @Mapping(target = "sagaId", ignore = true)
    Booking toEntity(BookingDTO bookingDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "bookingReference", ignore = true)
    void updateEntityFromDTO(BookingDTO bookingDTO, @MappingTarget Booking booking);
}