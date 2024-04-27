/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PracticaSI2;

import POJOS.Contribuyente;
import POJOS.HibernateUtil;
import POJOS.Recibos;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author ivanp
 */
public class PracticaSI2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        //practica4();
        practica5();
    }

    public static void practica4() throws Exception {
        // TODO code application logic here
        Session session = null;
        SessionFactory sf = null;
        Transaction tx = null;
        Contribuyente con = null;
        Recibos rec = null;
        Scanner sc = new Scanner(System.in); //System.in is a standard input stream
        System.out.print("Introduce un NIF de un contribuyente: ");
        String nif = sc.nextLine();              //reads string
        System.out.print("Has introducido: " + nif);
        session = HibernateUtil.getSessionFactory().openSession();

        String consultarNIF = "SELECT c FROM Contribuyente c WHERE c.nifnie =:param1 ";

        Query query = session.createQuery(consultarNIF);

        query.setParameter("param1", nif);

        List<Contribuyente> resultado = (List<Contribuyente>) query.list();

        if (resultado == null) {
            throw new Exception("no hay ningun contribuyente que coincida con el nif introducido");

        } else {
            System.out.println("El nombre del Contribuyente es: " + resultado.get(0).getNombre() + " " + resultado.get(0).getApellido1() + " " + resultado.get(0).getApellido2() + "\n nif: " + resultado.get(0).getNifnie() + "\n dirección: " + resultado.get(0).getDireccion());
            String consultarRecibos = "SELECT c FROM Recibos c WHERE c.nifContribuyente =:param1 ";
            query = session.createQuery(consultarRecibos);
            query.setParameter("param1", resultado.get(0).getNifnie());
            List<Recibos> recibos = (List<Recibos>) query.list();
            for (Recibos u : recibos) {
                u.setTotalRecibo(BigDecimal.valueOf(250));
                tx = session.beginTransaction();
                session.saveOrUpdate(u);
                tx.commit();
            }
        }
        String todosRecibos = "SELECT c FROM Recibos c";
        query = session.createQuery(todosRecibos);
        List<Recibos> recibos = (List<Recibos>) query.list();
        int mediaBI = 0;
        for (int i = 0; i < recibos.size(); i++) {
            mediaBI += recibos.get(i).getTotalBaseImponible().intValue();
        }
        mediaBI = mediaBI / recibos.size();
        System.out.println("la media de la base Imponible de todos los recibos es: " + mediaBI);
        int numeroRecibo = 0;
        for (Recibos u : recibos) {
            if (u.getTotalBaseImponible().intValue() < mediaBI) {
                System.out.println(u.getTotalBaseImponible().intValue());
                numeroRecibo = u.getNumeroRecibo();
                tx = session.beginTransaction();
                String HQLborrado = "DELETE Recibos r WHERE r.numeroRecibo=:param1";
                session.createQuery(HQLborrado).setParameter("param1", numeroRecibo).executeUpdate();
                tx.commit();
            }
        }
    }

    public static void practica5() throws FileNotFoundException, IOException, ParseException, XMLStreamException {

        InputStream inp = new FileInputStream("resources\\SistemasAgua.xlsx");
        ArrayList<Contribuyente> dniIncorrecto = new ArrayList<Contribuyente>();
        ArrayList<Contribuyente> cccIncorrecto = new ArrayList<Contribuyente>();
        ArrayList<String> cccSinCorregir = new ArrayList<String>();
        ArrayList<Contribuyente> contribuyentes = new ArrayList<Contribuyente>();
        Contribuyente aux;
        Workbook wb = WorkbookFactory.create(inp);
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(0);
        int l = 1;
        int i = 0;
        while (row != null) {
            aux = new Contribuyente();
            row = sheet.getRow(l++);
            if (row != null) {
                Cell nombre = row.getCell(0);
                Cell apellido1 = row.getCell(1);
                Cell apellido2 = row.getCell(2);
                Cell nif = row.getCell(3);
                Cell direccion = row.getCell(4);
                Cell numero = row.getCell(5);
                Cell paisCCC = row.getCell(6);
                Cell CCC = row.getCell(7);
                Cell iban = row.getCell(8);
                Cell email = row.getCell(9);
                Cell exencion = row.getCell(10);
                Cell bonificacion = row.getCell(11);
                Cell fechaAlta = row.getCell(14);
                Cell fechaBaja = row.getCell(15);
                if (nombre == null && apellido1 == null) {

                } else {
                    aux.setIdContribuyente(l);
                    aux.setNombre(nombre.getStringCellValue());
                    aux.setApellido1(apellido1.getStringCellValue());
                    if (apellido2 == null) {
                        aux.setApellido2("");
                    } else {
                        aux.setApellido2(apellido2.getStringCellValue());
                    }
                    if (nif == null) {
                        aux.setNifnie("");
                    } else {
                        aux.setNifnie(nif.getStringCellValue());
                    }
                    if (direccion == null) {
                        aux.setDireccion("");
                    } else {
                        aux.setDireccion(direccion.getStringCellValue());
                    }
                    if (numero == null) {
                        aux.setNumero("");
                    } else {
                        aux.setNumero(numero.getStringCellValue());
                    }
                    if (paisCCC == null) {
                        aux.setPaisCcc("");
                    } else {
                        aux.setPaisCcc(paisCCC.getStringCellValue());
                    }
                    if (CCC == null) {
                        aux.setCcc("");
                    } else {
                        aux.setCcc(CCC.getStringCellValue());
                    }
                    if (iban == null) {
                        aux.setIban("");

                    } else {
                        aux.setIban(iban.getStringCellValue());
                    }
                    if (email == null) {
                        aux.setEemail("");
                    } else {
                        aux.setEemail(email.getStringCellValue());
                    }
                    if (exencion == null) {
                        aux.setExencion("");
                    } else {
                        aux.setExencion(exencion.getStringCellValue());
                    }
                    if (bonificacion == null) {

                    } else {
                        /* BigDecimal bigDecimal = new BigDecimal(bonificacion.getStringCellValue());
                        aux.setBonificacion(bigDecimal);*/
                        aux.setBonificacion(BigDecimal.ZERO);
                    }
                    if (fechaAlta == null) {
                        //puede generar error cuando lo metamos en la base de datos
                    } else {
                        aux.setFechaAlta(fechaAlta.getDateCellValue());
                    }

                    if (fechaBaja == null) {
                        //puede generar error cuando lo metamos en la base de datos
                    } else {
                        aux.setFechaBaja(fechaBaja.getDateCellValue());
                    }
                    if (!aux.getNifnie().equals("") && validar(aux.getNifnie())!=0) {
                           if(validar(aux.getNifnie())==2){
                               String dni = cambiarLetra(aux.getNifnie());
                               aux.setNifnie(dni);
                               nif.setCellValue(dni);
                           }
                        String numccc = verificadorCCC(aux.getCcc());
                        if (!numccc.equals(aux.getCcc()) && !aux.getCcc().equals("")) {
                            cccSinCorregir.add(aux.getCcc());
                            aux.setCcc(numccc);
                            aux.setIban(generarIBAN(aux.getCcc(), aux.getPaisCcc()));
                            aux.setEemail(generarEmail(aux.getNombre(), aux.getApellido1(), aux.getApellido2(), contribuyentes));
                            CCC.setCellValue(aux.getCcc());
                            if(validar(aux.getNifnie())!=3 ){
                            iban = row.createCell(8);
                            iban.setCellValue(aux.getIban());
                            email = row.createCell(9);
                            email.setCellValue(aux.getEemail());
                            }
                            System.out.println("se ha cambiado el ccc de:" + aux.getIdContribuyente());
                          
                            cccIncorrecto.add(aux);
                        } else if (!aux.getCcc().equals("")&& validar(aux.getNifnie())!=3 ) {
                            aux.setIban(generarIBAN(aux.getCcc(), aux.getPaisCcc()));
                            aux.setEemail(generarEmail(aux.getNombre(), aux.getApellido1(), aux.getApellido2(), contribuyentes));
                            iban = row.createCell(8);
                            iban.setCellValue(aux.getIban());
                            email = row.createCell(9);
                            email.setCellValue(aux.getEemail());

                        }
                    }  
                    if (validar(aux.getNifnie())!=1){
                        
                        dniIncorrecto.add(aux);
                         
                    }
                }
                if(!contribuyentes.isEmpty()){  
                for(int j=0; j<contribuyentes.size(); j++){
                if(contribuyentes.get(j).getNifnie()!=null){
                if(contribuyentes.get(j).getNifnie().equals(aux.getNifnie())&& !aux.getNifnie().equals("")){
                    dniIncorrecto.add(aux);
                }
                }
                }
                }
                contribuyentes.add(aux);
                System.out.println(contribuyentes.get(i).getIdContribuyente());
                System.out.println(contribuyentes.get(i).getNombre());
                System.out.println(contribuyentes.get(i).getNifnie());
                System.out.println(contribuyentes.get(i).getIban());

            }
            i++;
        }
        //llamar al xml CCC
        cccXmlGenerator(cccIncorrecto, cccSinCorregir);
        //llamada al xml dni
        dniXmlGenerator(dniIncorrecto);
        FileOutputStream outputStream = new FileOutputStream("resources\\SistemasAgua.xlsx");
        wb.write(outputStream);
        wb.close();
    }

    public static void dniXmlGenerator(ArrayList<Contribuyente> contribuyentes) throws FileNotFoundException, XMLStreamException {
        XMLOutputFactory output1 = XMLOutputFactory.newInstance();

        XMLStreamWriter writer1 = output1.createXMLStreamWriter(new FileOutputStream("resources\\ErroresNifNie.xml"));

        writer1.writeStartDocument();

        writer1.writeStartElement("Contribuyentes");
        for (int i = 0; i < contribuyentes.size(); i++) {
            try {
                writer1.writeStartElement("Contribuyente ");
                writer1.writeAttribute("id", "" + contribuyentes.get(i).getIdContribuyente());
                writer1.writeStartElement("NIF_NIE");
                writer1.writeCharacters(contribuyentes.get(i).getNifnie());
                writer1.writeEndElement();
                writer1.writeStartElement("Nombre");
                writer1.writeCharacters(contribuyentes.get(i).getNombre());
                writer1.writeEndElement();
                writer1.writeStartElement("PrimerApellido");
                writer1.writeCharacters(contribuyentes.get(i).getApellido1());
                writer1.writeEndElement();
                writer1.writeStartElement("SegundoApellido");
                writer1.writeCharacters(contribuyentes.get(i).getApellido2());
                writer1.writeEndElement();
                writer1.writeEndElement();
            } catch (XMLStreamException ex) {
                Logger.getLogger(PracticaSI2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        writer1.writeEndElement();

        writer1.writeEndDocument();

        writer1.flush();

        writer1.close();
        System.out.println("Done");
    }
    
     public static void cccXmlGenerator(ArrayList<Contribuyente> contribuyentes, ArrayList<String> ccc) throws FileNotFoundException, XMLStreamException {
        XMLOutputFactory output1 = XMLOutputFactory.newInstance();

        XMLStreamWriter writer1 = output1.createXMLStreamWriter(new FileOutputStream("resources\\ErroresCCC.xml"));

        writer1.writeStartDocument();

        writer1.writeStartElement("Cuentas");
        for (int i = 0; i < contribuyentes.size(); i++) {
            try {
                writer1.writeStartElement("Cuenta ");
                writer1.writeAttribute("id", "" + contribuyentes.get(i).getIdContribuyente());
                
                writer1.writeStartElement("Nombre");
                writer1.writeCharacters(contribuyentes.get(i).getNombre());
                writer1.writeEndElement();
                writer1.writeStartElement("Apellidos");
                writer1.writeCharacters(contribuyentes.get(i).getApellido1()+" "+contribuyentes.get(i).getApellido2() );
                writer1.writeEndElement();
                writer1.writeStartElement("NIF_NIE");
                writer1.writeCharacters(contribuyentes.get(i).getNifnie());
                writer1.writeEndElement();
                writer1.writeStartElement("CCCErroneo");
                writer1.writeCharacters(ccc.get(i));
                writer1.writeEndElement();
                writer1.writeStartElement("IBANCorrecto");
                writer1.writeCharacters(contribuyentes.get(i).getIban());
                writer1.writeEndElement();
                writer1.writeEndElement();
            } catch (XMLStreamException ex) {
                Logger.getLogger(PracticaSI2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        writer1.writeEndElement();

        writer1.writeEndDocument();

        writer1.flush();

        writer1.close();
        System.out.println("Done");
    }
    public static int validar(String dni) {
        boolean x = true;
        char letra;
        int resto;
        String numero;
        char[] asignacionLetra = {
            'T',
            'R',
            'W',
            'A',
            'G',
            'M',
            'Y',
            'F',
            'P',
            'D',
            'X',
            'B',
            'N',
            'J',
            'Z',
            'S',
            'Q',
            'V',
            'H',
            'L',
            'C',
            'K',
            'E'
        };
        if (dni.equals("")) {
            return 0;
        } else if (dni.length() == 9 && Character.isLetter(dni.charAt(8)) && Character.isDigit(dni.charAt(0))) {
            numero = dni.substring(0, 8);
            int num = Integer.parseInt(numero);
            System.out.println("numero" + num);
            letra = dni.charAt(8);
            resto = num % 23;
            System.out.println("resto: " + resto + " " + "letra: " + letra);
            if(asignacionLetra[resto] == letra){
                return 1;
            }else{
                //caso de que este mal pero hay que generar el iban igual 
                return 2;
            }

        } else if (dni.length() == 9 && Character.isLetter(dni.charAt(0)) && Character.isLetter(dni.charAt(8)) && Character.isDigit(dni.charAt(1))) {
            switch (dni.substring(0, 1).toUpperCase()) {
                case "X":
                    dni = "0" + dni.substring(1, 9);
                    break;
                case "Y":
                    dni = "1" + dni.substring(1, 9);
                    break;
                case "Z":
                    dni = "2" + dni.substring(1, 9);
                    break;
                default:
                    return 3;
            }

            numero = dni.substring(0, 8);
            int num = Integer.parseInt(numero);
            System.out.println("dniiiiii: "+numero);
            letra = dni.charAt(8);
            
            resto = num % 23;
            System.out.println("resto: " + resto + " " + "letra: " + letra);
            
            switch (dni.substring(0, 1)) {
                case "0":
                    dni = "X" + dni.substring(1, 9);
                    break;
                case "1":
                    dni = "Y" + dni.substring(1, 9);
                    break;
                case "2":
                    dni = "Z" + dni.substring(1, 9);
                    break;
                default:
                    break;
            }
            if(asignacionLetra[resto] == letra){
                return 1;
            }else{
                return 2;
            }
            
           
        }
        return 3;
    }
    
     public static String cambiarLetra(String dni) {
        char letra;
        int resto;
        String numero;
        char[] asignacionLetra = {
            'T',
            'R',
            'W',
            'A',
            'G',
            'M',
            'Y',
            'F',
            'P',
            'D',
            'X',
            'B',
            'N',
            'J',
            'Z',
            'S',
            'Q',
            'V',
            'H',
            'L',
            'C',
            'K',
            'E'
        };
        if (dni.length() == 9 && Character.isLetter(dni.charAt(8)) && Character.isDigit(dni.charAt(0))) {
            numero = dni.substring(0, 8);
            int num = Integer.parseInt(numero);
            System.out.println("numero" + num);
            letra = dni.charAt(8);
            resto = num % 23;
            System.out.println("resto: " + resto + " " + "letra: " + letra);
            if(asignacionLetra[resto] == letra){
                return dni;
            }else{
                //caso de que este mal pero hay que generar el iban igual 
                return numero + asignacionLetra[resto];
            }

        } else if (dni.length() == 9 && Character.isLetter(dni.charAt(0)) && Character.isLetter(dni.charAt(8)) && Character.isDigit(dni.charAt(1))) {
            switch (dni.substring(0, 1).toUpperCase()) {
                case "X":
                    dni = "0" + dni.substring(1, 9);
                    break;
                case "Y":
                    dni = "1" + dni.substring(1, 9);
                    break;
                case "Z":
                    dni = "2" + dni.substring(1, 9);
                    break;
                default:
                     return dni;
            }

            numero = dni.substring(0, 8);
            int num = Integer.parseInt(numero);
            System.out.println("dniiiiii: "+numero);
            letra = dni.charAt(8);
            
            resto = num % 23;
            System.out.println("resto: " + resto + " " + "letra: " + letra);
            
            switch (dni.substring(0, 1)) {
                case "0":
                    dni = "X" + dni.substring(1, 9);
                    break;
                case "1":
                    dni = "Y" + dni.substring(1, 9);
                    break;
                case "2":
                    dni = "Z" + dni.substring(1, 9);
                    break;
                default:
                    break;
            }
            if(asignacionLetra[resto] == letra){
                 return dni;
            }else{
                return dni.substring(0,8)+ asignacionLetra[resto];
            }
            
           
        }
        return dni;
    }

    public static String verificadorCCC(String numCuenta) {
        if (numCuenta.length() != 20) {
            throw new IllegalArgumentException("El número de cuenta no puede tener más de 20 carácteres");
        }
        String digControl = numCuenta.substring(8, 10);
        String primerDig = "00" + numCuenta.substring(0, 8);
        String segundoDig = numCuenta.substring(10);
        int dig1 = generarDigitosControl(primerDig);
        int dig2 = generarDigitosControl(segundoDig);
        String digitos = "" + dig1 + dig2;
        return primerDig.substring(2) + digitos + segundoDig;
    }

    enum Letra {
        A(10), B(11), C(12), D(13), E(14), F(15), G(16), H(17), I(18), J(19), K(20), L(21), M(22), N(23), O(24), P(25), Q(26), R(27), S(28), T(29), U(30), V(31), W(32), X(33), Y(34), Z(35);
        private int valor;

        Letra(int valor) {
            this.valor = valor;
        }

        int getValor() {
            return valor;
        }
    }

    public static String generarIBAN(String numCuenta, String pais) {

        int dig1 = Letra.valueOf(pais.substring(0, 1)).getValor();
        int dig2 = Letra.valueOf(pais.substring(1)).getValor();
        numCuenta += "" + dig1 + dig2 + "00";
        BigInteger num = new BigInteger(numCuenta);
        BigInteger[] division = num.divideAndRemainder(new BigInteger("97"));
        int res = division[1].intValue();
        int digitos = 98 - res;
        if (digitos / 10 <= 0) {
            return pais + "0" + digitos + numCuenta.substring(0, numCuenta.length() - 6);
        }
        return pais + digitos + numCuenta.substring(0, numCuenta.length() - 6);
    }

    private static int generarDigitosControl(String genPrimDig) {
        int suma = 0;
        for (int i = 0; i < genPrimDig.length(); i++) {
            int digito = Integer.parseInt(genPrimDig.substring(i, i + 1));
            suma += digito * (Math.pow(2, i) % 11);
        }
        int dig1 = 11 - (suma % 11);
        dig1 = dig1 % 11;
        if (dig1 == 10) {
            dig1 = 1;
        }
        return dig1;
    }

    public static String generarEmail(String nombre, String apellido1, String apellido2, ArrayList<Contribuyente> contribuyentes) {
        String apellidoPersona2;
        if (nombre.equals("") && apellido1.equals("")) {
            return "";
        }
        if (apellido2 != null) {
            if (apellido2 == "") {
                apellidoPersona2 = "";
            } else {
                //System.out.println(apellido2);
                apellidoPersona2 = apellido2.substring(0, 1);
            }
        } else {
            apellidoPersona2 = "";
        }
        String apellidoPersona1 = apellido1.substring(0, 1);
        String nombrePersona = nombre.substring(0, 1);
        String numero;
        int contador = 0;
        if (!contribuyentes.isEmpty()) {
            for (int i = 0; i < contribuyentes.size(); i++) {
                if (contribuyentes.get(i).getNombre() != null) {
                    if (apellidoPersona2.equals("")) {
                        if (nombrePersona.equals(contribuyentes.get(i).getNombre().substring(0, 1)) && apellidoPersona1.equals(contribuyentes.get(i).getApellido1().substring(0, 1))) {
                            contador++;
                        }
                    } else {
                        if (contribuyentes.get(i).getApellido2().equals("")) {

                        } else {
                            if (nombrePersona.equals(contribuyentes.get(i).getNombre().substring(0, 1)) && apellidoPersona1.equals(contribuyentes.get(i).getApellido1().substring(0, 1)) && apellidoPersona2.equals(contribuyentes.get(i).getApellido2().substring(0, 1))) {

                                contador++;
                            }
                        }

                    }

                }
            }
        }
        if (contador < 10) {
            numero = "0" + Integer.toString(contador);
        } else {
            numero = Integer.toString(contador);
        }

        String correo = nombrePersona + apellidoPersona1 + apellidoPersona2 + numero + "@" + "Agua2024" + ".com";

        return correo;
    }
}
