/*
 *  OpenSDI Manager
 *  Copyright (C) 2013 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.opensdi.service.impl;

import java.io.Serializable;

import it.geosolutions.opensdi.service.GeoBatchClient;

/**
 * Simple envelop to use same parameters for GeoBatch connection on all controllers
 * 
 * @author adiaz
 */
public class GeoBatchClientImpl implements GeoBatchClient, Serializable {

/** serialVersionUID */
private static final long serialVersionUID = 7299932957642777660L;

private String geobatchRestUrl;
private String geobatchUsername;
private String geobatchPassword;

/**
 * @return the geobatchRestUrl
 */
public String getGeobatchRestUrl() {
    return geobatchRestUrl;
}

/**
 * @param geobatchRestUrl the geobatchRestUrl to set
 */
public void setGeobatchRestUrl(String geobatchRestUrl) {
    this.geobatchRestUrl = geobatchRestUrl;
}

/**
 * @return the geobatchUsername
 */
public String getGeobatchUsername() {
    return geobatchUsername;
}

/**
 * @param geobatchUsername the geobatchUsername to set
 */
public void setGeobatchUsername(String geobatchUsername) {
    this.geobatchUsername = geobatchUsername;
}

/**
 * @return the geobatchPassword
 */
public String getGeobatchPassword() {
    return geobatchPassword;
}

/**
 * @param geobatchPassword the geobatchPassword to set
 */
public void setGeobatchPassword(String geobatchPassword) {
    this.geobatchPassword = geobatchPassword;
}

}
