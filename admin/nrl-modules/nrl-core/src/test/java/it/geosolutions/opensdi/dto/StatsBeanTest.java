/*
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
package it.geosolutions.opensdi.dto;

import it.geosolutions.opensdi.dto.StatsBean;

import javax.xml.bind.JAXB;

import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class StatsBeanTest  {

    public StatsBeanTest() {
    }

    /**
     * Test of parse method, of class Season.
     */
    @Test
    public void testSerializeStd() {
        StatsBean sb = new StatsBean();
        sb.setClassifier(StatsBean.CLASSIFIER_TYPE.PROVINCE);
        sb.setForestMask(StatsBean.MASK_TYPE.STANDARD);
        sb.setNdviFileName("98081");

        JAXB.marshal(sb, System.out);
    }

    @Test
    public void testSerializeCst() {
        StatsBean sb = new StatsBean();
        sb.setClassifier(StatsBean.CLASSIFIER_TYPE.CUSTOM);
        sb.setClassifierFullPath("/this/is/a/full/path");
        sb.setForestMask(StatsBean.MASK_TYPE.CUSTOM);
        sb.setForestMaskFullPath("/a/full/path/for/forest/mask");
        sb.setNdviFileName("98081");

        JAXB.marshal(sb, System.out);
    }



}