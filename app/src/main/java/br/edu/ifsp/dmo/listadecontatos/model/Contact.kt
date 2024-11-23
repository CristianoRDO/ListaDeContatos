package br.edu.ifsp.dmo.listadecontatos.model

class Contact(val name: String, val phone: String) {
    override fun toString(): String {
        return "Contato{name: '$name', phone: '$phone'}"
    }
}